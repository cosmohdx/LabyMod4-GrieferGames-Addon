package de.cosmohdx.griefergames.feature.itempreview;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class HeadProfileTexturesTest {

  @Test
  void readsTheSkinUrlAndSlimModelFromTheTexturesPayload() {
    String payload = encode("{\"textures\":{\"SKIN\":{\"url\":\"https://textures.example/skin\",\"metadata\":{\"model\":\"slim\"}}}}");

    HeadTexture texture = HeadProfileTextures.fromTexturesValue(payload);

    assertNotNull(texture);
    assertEquals("https://textures.example/skin", texture.url());
    assertTrue(texture.slim());
    assertEquals(3, texture.textureId().version());
  }

  @Test
  void returnsNullForAPayloadWithoutASkin() {
    assertNull(HeadProfileTextures.fromTexturesValue(encode("{\"textures\":{}}")));
    assertNull(HeadProfileTextures.fromTexturesValue("not-base64"));
    assertNull(HeadProfileTextures.fromTexturesValue(null));
  }

  @Test
  void parsesHyphenatedAndCompactUuids() {
    UUID uuid = UUID.fromString("853c80ef-3c37-49fd-aa49-938b674adae6");

    assertEquals(uuid, HeadProfileTextures.parseUuid(uuid.toString()));
    assertEquals(uuid, HeadProfileTextures.parseUuid("853c80ef3c3749fdaa49938b674adae6"));
    assertNull(HeadProfileTextures.parseUuid("nope"));
  }

  @Test
  void rebuildsAUuidFromTheIntArrayForm() {
    UUID uuid = UUID.fromString("853c80ef-3c37-49fd-aa49-938b674adae6");
    int[] parts = new int[] {
        (int) (uuid.getMostSignificantBits() >> 32),
        (int) uuid.getMostSignificantBits(),
        (int) (uuid.getLeastSignificantBits() >> 32),
        (int) uuid.getLeastSignificantBits()
    };

    assertEquals(uuid, HeadProfileTextures.uuidFromIntArray(parts));
    assertNull(HeadProfileTextures.uuidFromIntArray(new int[] {1, 2}));
    assertFalse(new HeadTexture(null, null, null, false).canRender());
  }

  private static String encode(String json) {
    return Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
  }
}
