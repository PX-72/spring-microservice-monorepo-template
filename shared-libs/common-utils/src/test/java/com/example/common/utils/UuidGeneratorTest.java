package com.example.common.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class UuidGeneratorTest {

  @AfterEach
  void tearDown() {
    UuidGenerator.reset();
  }

  @Test
  void generate_returnsRandomUuid() {
    var generator = UuidGenerator.getInstance();

    var uuid1 = generator.generate();
    var uuid2 = generator.generate();

    assertNotNull(uuid1);
    assertNotNull(uuid2);
    assertNotEquals(uuid1, uuid2);
  }

  @Test
  void parseOrNull_parsesValidUuid() {
    var generator = UuidGenerator.getInstance();
    var expected = UUID.randomUUID();

    var result = generator.parseOrNull(expected.toString());

    assertEquals(expected, result);
  }

  @Test
  void parseOrNull_returnsNullForInvalidUuid() {
    var generator = UuidGenerator.getInstance();

    assertNull(generator.parseOrNull("not-a-uuid"));
    assertNull(generator.parseOrNull("12345"));
    assertNull(generator.parseOrNull(""));
    assertNull(generator.parseOrNull(null));
  }

  @Test
  void parseOrNull_trimsWhitespace() {
    var generator = UuidGenerator.getInstance();
    var uuid = UUID.randomUUID();

    var result = generator.parseOrNull("  " + uuid + "  ");

    assertEquals(uuid, result);
  }

  @Test
  void parseOrNull_returnsNullForBlankString() {
    var generator = UuidGenerator.getInstance();

    assertNull(generator.parseOrNull("   "));
  }

  @Test
  void isValid_returnsTrueForValidUuid() {
    var generator = UuidGenerator.getInstance();

    assertTrue(generator.isValid(UUID.randomUUID().toString()));
    assertTrue(generator.isValid("550e8400-e29b-41d4-a716-446655440000"));
  }

  @Test
  void isValid_returnsFalseForInvalidUuid() {
    var generator = UuidGenerator.getInstance();

    assertFalse(generator.isValid("not-a-uuid"));
    assertFalse(generator.isValid(""));
    assertFalse(generator.isValid(null));
  }

  @Test
  void setInstance_allowsCustomGenerator() {
    var fixedUuid = UUID.fromString("11111111-1111-1111-1111-111111111111");
    var customGenerator =
        new UuidGenerator() {
          @Override
          public UUID generate() {
            return fixedUuid;
          }
        };

    UuidGenerator.setInstance(customGenerator);

    assertEquals(fixedUuid, UuidGenerator.getInstance().generate());
  }

  @Test
  void reset_restoresDefaultGenerator() {
    var fixedUuid = UUID.fromString("11111111-1111-1111-1111-111111111111");
    var customGenerator =
        new UuidGenerator() {
          @Override
          public UUID generate() {
            return fixedUuid;
          }
        };
    UuidGenerator.setInstance(customGenerator);

    UuidGenerator.reset();

    assertNotEquals(fixedUuid, UuidGenerator.getInstance().generate());
  }

  @Test
  void setInstance_withNullResetsToDefault() {
    var fixedUuid = UUID.fromString("11111111-1111-1111-1111-111111111111");
    var customGenerator =
        new UuidGenerator() {
          @Override
          public UUID generate() {
            return fixedUuid;
          }
        };
    UuidGenerator.setInstance(customGenerator);

    UuidGenerator.setInstance(null);

    assertNotEquals(fixedUuid, UuidGenerator.getInstance().generate());
  }
}
