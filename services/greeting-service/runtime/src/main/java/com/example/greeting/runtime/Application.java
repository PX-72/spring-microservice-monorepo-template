package com.example.greeting.runtime;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.example.greeting")
@EnableJpaRepositories(basePackages = "com.example.greeting.adapters.out.persistence")
@EntityScan(basePackages = "com.example.greeting.adapters.out.persistence")
public class Application {
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
