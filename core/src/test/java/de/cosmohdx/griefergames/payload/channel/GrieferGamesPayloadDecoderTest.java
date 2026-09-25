package de.cosmohdx.griefergames.payload.channel;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.cosmohdx.griefergames.payload.ClientPayload;
import de.cosmohdx.griefergames.payload.PayloadCodec;
import de.cosmohdx.griefergames.payload.model.AccountBalancePayload;
import de.cosmohdx.griefergames.payload.model.BankBalancePayload;
import de.cosmohdx.griefergames.payload.model.BlockOfTheDayPayload;
import de.cosmohdx.griefergames.payload.model.BoosterPayload;
import de.cosmohdx.griefergames.payload.model.ClearLagPayload;
import de.cosmohdx.griefergames.payload.model.EntityRemoverPayload;
import de.cosmohdx.griefergames.payload.model.LootTableProgressPayload;
import de.cosmohdx.griefergames.payload.model.PlotChatConfigurationPayload;
import de.cosmohdx.griefergames.payload.model.UnknownPayload;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.IOException;
import org.junit.jupiter.api.Test;

class GrieferGamesPayloadDecoderTest {

  private final GrieferGamesPayloadDecoder decoder = new GrieferGamesPayloadDecoder();

  @Test
  void decodesEveryBuiltinPayload() throws IOException {
    assertEquals(12.5, assertInstanceOf(AccountBalancePayload.class,
        decoder.decode(frame("accountbalance", out -> out.writeDouble(12.5)))).balance());
    assertEquals(3.0, assertInstanceOf(BankBalancePayload.class,
        decoder.decode(frame("bankbalance", out -> out.writeDouble(3.0)))).balance());
    assertTrue(assertInstanceOf(PlotChatConfigurationPayload.class,
        decoder.decode(frame("plotchat_configuration", out -> out.writeBoolean(true)))).enabled());
    assertEquals(90L, assertInstanceOf(ClearLagPayload.class,
        decoder.decode(frame("clearlag", out -> out.writeLong(90L)))).remainingSeconds());
    assertEquals(-1L, assertInstanceOf(EntityRemoverPayload.class,
        decoder.decode(frame("entityremover", out -> out.writeLong(-1L)))).remainingSeconds());

    BoosterPayload boosters = assertInstanceOf(BoosterPayload.class, decoder.decode(frame("booster", out -> {
      out.writeInt(2);
      out.writeUTF("BREAK");
      out.writeInt(2);
      out.writeUTF("FLY");
      out.writeInt(1);
    })));
    assertEquals(2, boosters.boosters().size());
    assertEquals("BREAK", boosters.boosters().get(0).type());
    assertEquals(2, boosters.boosters().get(0).multiplier());
    assertEquals("FLY", boosters.boosters().get(1).type());

    BlockOfTheDayPayload block = assertInstanceOf(BlockOfTheDayPayload.class, decoder.decode(frame("blockoftheday", out -> {
      out.writeUTF("BLOCK");
      out.writeUTF("DIAMOND_ORE");
      out.writeInt(56);
      out.writeUTF("");
    })));
    assertEquals("BLOCK", block.type());
    assertEquals("DIAMOND_ORE", block.blockMaterial());
    assertEquals(56, block.blockData());
    assertEquals("", block.entityType());

    assertInstanceOf(LootTableProgressPayload.class, decoder.decode(frame("blockoftheday_progress", out -> {
    })));
  }

  @Test
  void idIsModifiedUtfBeforeTheBody() throws IOException {
    byte[] payload = frame("accountbalance", out -> out.writeDouble(1.0));
    assertEquals(0, payload[0]);
    assertEquals("accountbalance".length(), payload[1] & 0xFF);
    assertEquals('a', payload[2]);
  }

  @Test
  void unknownIdKeepsTheUnreadBody() throws IOException {
    byte[] payload = frame("future_payload", out -> {
      out.writeInt(7);
      out.writeUTF("later");
    });
    UnknownPayload unknown = assertInstanceOf(UnknownPayload.class, decoder.decode(payload));
    assertEquals("future_payload", unknown.id());

    byte[] expectedBody = frameBody(out -> {
      out.writeInt(7);
      out.writeUTF("later");
    });
    assertArrayEquals(expectedBody, unknown.data());
  }

  @Test
  void trailingBytesOnAKnownPayloadAreIgnored() throws IOException {
    byte[] payload = frame("accountbalance", out -> {
      out.writeDouble(4.0);
      out.writeInt(99);
    });
    assertEquals(4.0, assertInstanceOf(AccountBalancePayload.class, decoder.decode(payload)).balance());
  }

  @Test
  void laterCodecCanBeRegistered() throws IOException {
    decoder.register(PayloadCodec.of("future_payload", (DataInput input) -> new NamedPayload(input.readUTF())));
    NamedPayload payload = assertInstanceOf(NamedPayload.class,
        decoder.decode(frame("future_payload", out -> out.writeUTF("ok"))));
    assertEquals("ok", payload.name);
  }

  @Test
  void rejectsAHugeBoosterList() {
    assertThrows(IOException.class, () -> decoder.decode(frame("booster", out -> out.writeInt(10_000))));
  }

  @Test
  void truncatedPayloadFails() throws IOException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    new DataOutputStream(bytes).writeUTF("accountbalance");
    assertThrows(IOException.class, () -> decoder.decode(bytes.toByteArray()));
  }

  private static byte[] frame(String id, Body body) throws IOException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    DataOutputStream out = new DataOutputStream(bytes);
    out.writeUTF(id);
    body.write(out);
    return bytes.toByteArray();
  }

  private static byte[] frameBody(Body body) throws IOException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    body.write(new DataOutputStream(bytes));
    return bytes.toByteArray();
  }

  @FunctionalInterface
  private interface Body {
    void write(DataOutputStream out) throws IOException;
  }

  private record NamedPayload(String name) implements ClientPayload {
    @Override
    public String id() {
      return "future_payload";
    }
  }
}
