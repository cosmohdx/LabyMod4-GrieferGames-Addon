package de.cosmohdx.griefergames.payload;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.cosmohdx.griefergames.payload.model.BlockOfTheDayPayload;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.junit.jupiter.api.Test;

class PayloadLogTest {

  @Test
  void peekIdReadsTheLeadingModifiedUtf() throws IOException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    DataOutputStream out = new DataOutputStream(bytes);
    out.writeUTF("blockoftheday");
    out.writeUTF("MATERIAL");

    assertEquals("blockoftheday", PayloadLog.peekId(bytes.toByteArray()));
  }

  @Test
  void peekIdSurvivesGarbage() {
    assertEquals("(empty)", PayloadLog.peekId(null));
    assertEquals("(empty)", PayloadLog.peekId(new byte[] {0}));
    assertEquals("(unreadable)", PayloadLog.peekId(new byte[] {0x7F, 0x00}));
  }

  @Test
  void describeKeepsABlockPayloadAndTruncatesLongText() {
    BlockOfTheDayPayload payload = new BlockOfTheDayPayload("MATERIAL", "DIAMOND_ORE", 0, "");
    assertTrue(PayloadLog.describe(payload).contains("DIAMOND_ORE"));

    String longJson = "x".repeat(300);
    String described = PayloadLog.describe(new NamedPayload(longJson));
    assertEquals(241, described.length());
    assertTrue(described.endsWith("…"));
  }

  @Test
  void hexPrefixIsLowercaseAndStopsAfter24Bytes() {
    byte[] raw = new byte[30];
    raw[0] = 0x0A;
    raw[1] = (byte) 0xFF;
    assertEquals("0aff", PayloadLog.hexPrefix(raw).substring(0, 4));
    assertTrue(PayloadLog.hexPrefix(raw).endsWith("…"));
    assertEquals("", PayloadLog.hexPrefix(null));
  }

  private record NamedPayload(String name) implements ClientPayload {
    @Override
    public String id() {
      return "named";
    }
  }
}
