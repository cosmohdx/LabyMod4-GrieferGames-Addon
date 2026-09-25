package de.cosmohdx.griefergames.feature.chat;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Decides which outgoing lines may be split, and rewrites {@code /r} into {@code /msg} when a
 * partner is known. Global chat ({@code @}) and every other command stay as the player typed them.
 */
public final class OutgoingChat {

  private static final Pattern MESSAGE = Pattern.compile("^(?i)/msg\\s+\\S+\\s+\\S");
  private static final Pattern REPLY = Pattern.compile("^(?i)/r(?:\\s+(\\S[\\s\\S]*))?$");

  private OutgoingChat() {
  }

  /**
   * @return the line to split, or empty when this line must be sent unchanged
   */
  public static Optional<String> textToSplit(String message, boolean splitPrivate,
      Optional<String> partner) {
    if (message == null || message.isEmpty()) {
      return Optional.empty();
    }
    String visible = stripLeadingColors(message);
    if (visible.startsWith("@") || visible.startsWith(".") || visible.startsWith("-")) {
      return Optional.empty();
    }
    if (!visible.startsWith("/")) {
      return Optional.of(message);
    }
    if (MESSAGE.matcher(visible).find()) {
      return splitPrivate ? Optional.of(message) : Optional.empty();
    }
    Matcher reply = REPLY.matcher(visible);
    if (!reply.matches()) {
      return Optional.empty();
    }
    if (!splitPrivate || partner == null || partner.isEmpty()) {
      return Optional.empty();
    }
    String rest = reply.group(1);
    if (rest == null || rest.isBlank()) {
      return Optional.empty();
    }
    return Optional.of("/msg " + partner.get() + " " + rest);
  }

  static String stripLeadingColors(String message) {
    int index = 0;
    while (index + 1 < message.length() && message.charAt(index) == '&') {
      char code = Character.toLowerCase(message.charAt(index + 1));
      if (!LongMessageSplitter.isLegacyCode(code)) {
        break;
      }
      index += 2;
    }
    return message.substring(index);
  }
}
