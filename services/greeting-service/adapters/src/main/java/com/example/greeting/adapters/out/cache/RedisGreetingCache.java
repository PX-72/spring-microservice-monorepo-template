package com.example.greeting.adapters.out.cache;

import com.example.greeting.domain.Greeting;
import com.example.greeting.domain.ports.out.GreetingCache;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.time.Duration;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisGreetingCache implements GreetingCache {

  private static final Logger logger = LoggerFactory.getLogger(RedisGreetingCache.class);
  private static final String KEY_PREFIX = "greeting:";
  private static final Duration TTL = Duration.ofMinutes(30);

  private final RedisTemplate<String, Greeting> redisTemplate;
  private final Counter cacheHitCounter;
  private final Counter cacheMissCounter;
  private final Timer cacheGetTimer;
  private final Timer cachePutTimer;

  public RedisGreetingCache(
      RedisTemplate<String, Greeting> redisTemplate, MeterRegistry meterRegistry) {
    this.redisTemplate = redisTemplate;

    this.cacheHitCounter =
        Counter.builder("cache.greeting.hits")
            .description("Number of cache hits")
            .tag("cache", "redis")
            .register(meterRegistry);
    this.cacheMissCounter =
        Counter.builder("cache.greeting.misses")
            .description("Number of cache misses")
            .tag("cache", "redis")
            .register(meterRegistry);
    this.cacheGetTimer =
        Timer.builder("cache.greeting.get")
            .description("Time to get from cache")
            .tag("cache", "redis")
            .register(meterRegistry);
    this.cachePutTimer =
        Timer.builder("cache.greeting.put")
            .description("Time to put into cache")
            .tag("cache", "redis")
            .register(meterRegistry);
  }

  @Override
  public Optional<Greeting> get(UUID id) {
    logger.debug("Cache GET for greeting id={}", id);
    return cacheGetTimer.record(
        () -> {
          try {
            var value = redisTemplate.opsForValue().get(keyFor(id));
            if (value != null) {
              cacheHitCounter.increment();
              logger.debug("Cache HIT for greeting id={}", id);
            } else {
              cacheMissCounter.increment();
              logger.debug("Cache MISS for greeting id={}", id);
            }
            return Optional.ofNullable(value);
          } catch (Exception e) {
            logger.error("Cache GET failed for greeting id={}: {}", id, e.getMessage());
            cacheMissCounter.increment();
            return Optional.empty();
          }
        });
  }

  @Override
  public void put(Greeting greeting) {
    logger.debug("Cache PUT for greeting id={}", greeting.id());
    cachePutTimer.record(
        () -> {
          try {
            redisTemplate.opsForValue().set(keyFor(greeting.id()), greeting, TTL);
            logger.info("Cached greeting id={} with TTL={}min", greeting.id(), TTL.toMinutes());
          } catch (Exception e) {
            logger.error("Cache PUT failed for greeting id={}: {}", greeting.id(), e.getMessage());
          }
        });
  }

  @Override
  public void evict(UUID id) {
    logger.debug("Cache EVICT for greeting id={}", id);
    try {
      redisTemplate.delete(keyFor(id));
      logger.info("Evicted greeting id={} from cache", id);
    } catch (Exception e) {
      logger.error("Cache EVICT failed for greeting id={}: {}", id, e.getMessage());
    }
  }

  @Override
  public void evictAll() {
    logger.debug("Cache EVICT ALL");
    try {
      Set<String> keys = redisTemplate.keys(KEY_PREFIX + "*");
      if (keys != null && !keys.isEmpty()) {
        redisTemplate.delete(keys);
        logger.info("Evicted {} greeting entries from cache", keys.size());
      }
    } catch (Exception e) {
      logger.error("Cache EVICT ALL failed: {}", e.getMessage());
    }
  }

  private String keyFor(UUID id) {
    return KEY_PREFIX + id.toString();
  }
}
