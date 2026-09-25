package de.cosmohdx.griefergames.payload;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

/**
 * Short text for the payload debug log. No LabyMod calls, so tests can use it directly.
 */
public final class PayloadLog {

  private static final int MAX_TEXT = 240;
  private static final int MAX_HEX_BYTES = 24;

  private PayloadLog() {
  }

  public static String peekId(byte[] raw) {
    if (raw == null || raw.length < 2) {
      return "(empty)";
    }
    try {
      return new DataInputStream(new ByteArrayInputStream(raw)).readUTF();
    } catch (IOException exception) {
      return "(unreadable)";
    }
  }

  public static String describe(ClientPayload payload) {
    String text = String.valueOf(payload);
    if (text.length() <= MAX_TEXT) {
      return text;
    }
    return text.substring(0, MAX_TEXT) + "…";
  }

  public static String hexPrefix(byte[] raw) {
    if (raw == null || raw.length == 0) {
      return "";
    }
    int length = Math.min(raw.length, MAX_HEX_BYTES);
    StringBuilder hex = new StringBuilder(length * 2);
    for (int index = 0; index < length; index++) {
      hex.append(Character.forDigit((raw[index] >> 4) & 0xF, 16));
      hex.append(Character.forDigit(raw[index] & 0xF, 16));
    }
    if (raw.length > length) {
      hex.append("…");
    }
    return hex.toString();
  }
}
