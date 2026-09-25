package de.cosmohdx.griefergames.payload.channel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.cosmohdx.griefergames.payload.model.MysteryModMessage;
import de.cosmohdx.griefergames.payload.model.RedstoneStatusPayload;
import de.cosmohdx.griefergames.payload.model.UserSubtitlePayload;
import java.io.IOException;
import java.util.UUID;
import net.labymod.serverapi.api.payload.io.PayloadWriter;
import org.junit.jupiter.api.Test;

class MysteryModPayloadDecoderTest {

  private final MysteryModPayloadDecoder decoder = new MysteryModPayloadDecoder();

  @Test
  void redstoneStatusZeroIsActive() throws IOException {
    RedstoneStatusPayload payload = assertInstanceOf(RedstoneStatusPayload.class,
        decoder.decode(message("redstone", "{\"status\":\"0\"}")).orElseThrow());
    assertTrue(payload.active());
  }

  @Test
  void anyOtherRedstoneStatusIsInactive() throws IOException {
    assertFalse(assertInstanceOf(RedstoneStatusPayload.class,
        decoder.decode(message("redstone", "{\"status\":\"1\"}")).orElseThrow()).active());
    assertFalse(assertInstanceOf(RedstoneStatusPayload.class,
        decoder.decode(message("redstone", "{\"status\":2}")).orElseThrow()).active());
  }

  @Test
  void readsTheFirstUserSubtitle() throws IOException {
    UUID target = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    String json = "[{\"text\":\"&6Hallo\",\"targetId\":\"" + target + "\"},{\"text\":\"ignored\",\"targetId\":\""
        + target + "\"}]";
    UserSubtitlePayload payload = assertInstanceOf(UserSubtitlePayload.class,
        decoder.decode(message("user_subtitle", json)).orElseThrow());
    assertEquals(target, payload.targetId());
    assertEquals("&6Hallo", payload.text());
  }

  @Test
  void emptySubtitleIsIgnored() throws IOException {
    assertTrue(decoder.decode(message("user_subtitle", "[]")).isEmpty());
  }

  @Test
  void unknownKeyKeepsTheRawJson() throws IOException {
    MysteryModMessage message = assertInstanceOf(MysteryModMessage.class,
        decoder.decode(message("some_future_key", "{\"a\":1}")).orElseThrow());
    assertEquals("some_future_key", message.id());
    assertEquals("{\"a\":1}", message.json());
  }

  @Test
  void brokenFramingFails() {
    assertThrows(IOException.class, () -> decoder.decode(new byte[] {1, 2, 3}));
  }

  private static byte[] message(String key, String json) {
    PayloadWriter writer = new PayloadWriter();
    writer.writeString(key);
    writer.writeString(json);
    return writer.toByteArray();
  }
}
