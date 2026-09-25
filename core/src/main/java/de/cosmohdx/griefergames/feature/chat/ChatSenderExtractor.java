package de.cosmohdx.griefergames.feature.chat;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Reads the player name from a GrieferGames chat line.
 *
 * <p>Only lines a player actually wrote match: citybuild and global chat, private messages and
 * plot chat. Payments, status lines and other system text stay empty so they do not gain a head.
 */
public final class ChatSenderExtractor {

  private static final Pattern PRIVATE_IN = Pattern.compile(
      "^\\[" + SecondChatFormats.RANK_NAME + " -> (?:mir|me)\\]");
  private static final Pattern PRIVATE_OUT = Pattern.compile(
      "^\\[(?:mir|me) -> " + SecondChatFormats.RANK_NAME + "\\]");
  private static final Pattern PLOT_RANK = Pattern.compile(
      "^\\[Plot-Chat\\]\\s+" + SecondChatFormats.RANK_NAME + "\\s*:");
  private static final Pattern PLOT_NAME = Pattern.compile(
      "^\\[Plot-Chat\\]\\s+(~?\\!?\\w{1,16})\\s*:");
  private static final Pattern PUBLIC = Pattern.compile(
      "^(?:\\[[^\\]]+\\]\\s*)?" + SecondChatFormats.RANK_NAME + "\\s+\u00BB");

  private ChatSenderExtractor() {
  }

  public static Optional<ChatSender> sender(String plainText) {
    String text = normalize(plainText);
    if (text.isEmpty()) {
      return Optional.empty();
    }
    ChatSender incoming = match(PRIVATE_IN, text, 2, ChatSender.Kind.PRIVATE);
    if (incoming != null) {
      return Optional.of(incoming);
    }
    ChatSender outgoing = match(PRIVATE_OUT, text, 2, ChatSender.Kind.PRIVATE);
    if (outgoing != null) {
      return Optional.of(outgoing);
    }
    ChatSender plotRank = match(PLOT_RANK, text, 2, ChatSender.Kind.PLOT);
    if (plotRank != null) {
      return Optional.of(plotRank);
    }
    ChatSender plotName = match(PLOT_NAME, text, 1, ChatSender.Kind.PLOT);
    if (plotName != null) {
      return Optional.of(plotName);
    }
    ChatSender citybuild = match(PUBLIC, text, 2, ChatSender.Kind.PUBLIC);
    if (citybuild != null) {
      return Optional.of(citybuild);
    }
    return Optional.empty();
  }

  private static ChatSender match(Pattern pattern, String text, int nameGroup, ChatSender.Kind kind) {
    Matcher matcher = pattern.matcher(text);
    if (!matcher.find()) {
      return null;
    }
    String name = matcher.group(nameGroup);
    if (name == null || name.isBlank()) {
      return null;
    }
    return new ChatSender(name, kind);
  }

  static String normalize(String plainText) {
    if (plainText == null) {
      return "";
    }
    String text = plainText;
    while (text.length() >= 2 && text.charAt(0) == '\u00A7') {
      text = text.substring(2);
    }
    return text.stripLeading();
  }
}
