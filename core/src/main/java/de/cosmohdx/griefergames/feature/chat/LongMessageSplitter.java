package de.cosmohdx.griefergames.feature.chat;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Splits an outgoing 1.8 chat line on spaces.
 *
 * <p>The limit counts Unicode characters and includes a {@code /msg} prefix and colour codes.
 * A word that does not fit, including a link, is refused whole. Nothing is cut inside a word.
 * Each later part repeats the colour that was active at the end of the previous part.
 */
public final class LongMessageSplitter {

  /**
   * Characters per part on the 1.8 network, including prefix and colour codes.
   * Confirm the live limit in game. The 1.8.9 input box stops at this length on its own.
   */
  public static final int LEGACY_CHAT_LIMIT = 100;

  private static final Pattern MESSAGE_PREFIX = Pattern.compile("^(?i)(/msg\\s+\\S+\\s+)");

  private LongMessageSplitter() {
  }

  public static int length(String text) {
    if (text == null || text.isEmpty()) {
      return 0;
    }
    return text.codePointCount(0, text.length());
  }

  public static SplitResult split(String text, int limit, int maxParts) {
    if (text == null) {
      return new Parts(List.of());
    }
    if (limit < 1 || maxParts < 1) {
      return new TooLong();
    }
    if (length(text) <= limit) {
      return new Parts(List.of(text));
    }

    String prefix = "";
    String body = text;
    Matcher prefixMatcher = MESSAGE_PREFIX.matcher(text);
    if (prefixMatcher.find()) {
      prefix = prefixMatcher.group(1);
      body = text.substring(prefixMatcher.end());
    }
    if (length(prefix) >= limit) {
      return new WordTooLong();
    }

    List<String> words = words(body);
    if (words.isEmpty()) {
      return new TooLong();
    }

    List<String> parts = new ArrayList<>();
    StringBuilder current = new StringBuilder();
    for (String word : words) {
      String candidate = current.isEmpty() ? word : current + " " + word;
      if (length(prefix) + length(candidate) <= limit) {
        current.setLength(0);
        current.append(candidate);
        continue;
      }
      if (current.isEmpty()) {
        return new WordTooLong();
      }
      parts.add(prefix + current);
      if (parts.size() == maxParts) {
        return new TooLong();
      }
      String restarted = applyCarry(lastColor(current.toString()), word);
      if (length(prefix) + length(restarted) > limit) {
        return new WordTooLong();
      }
      current.setLength(0);
      current.append(restarted);
    }
    if (!current.isEmpty()) {
      parts.add(prefix + current);
    }
    if (parts.size() > maxParts) {
      return new TooLong();
    }
    return new Parts(List.copyOf(parts));
  }

  private static List<String> words(String body) {
    List<String> words = new ArrayList<>();
    int start = 0;
    for (int index = 0; index < body.length();) {
      int codePoint = body.codePointAt(index);
      int next = index + Character.charCount(codePoint);
      if (codePoint == ' ') {
        if (index > start) {
          words.add(body.substring(start, index));
        }
        start = next;
      }
      index = next;
    }
    if (start < body.length()) {
      words.add(body.substring(start));
    }
    return words;
  }

  static String lastColor(String text) {
    String found = "";
    for (int index = 0; index < text.length();) {
      int codePoint = text.codePointAt(index);
      int next = index + Character.charCount(codePoint);
      if (codePoint == '&' && next < text.length()) {
        int code = text.codePointAt(next);
        if (Character.isBmpCodePoint(code)) {
          char color = Character.toLowerCase((char) code);
          if (isColor(color)) {
            found = "&" + color;
          }
          if (isLegacyCode(color)) {
            index = next + Character.charCount(code);
            continue;
          }
        }
      }
      index = next;
    }
    return found;
  }

  private static String applyCarry(String carry, String word) {
    if (carry.isEmpty() || startsWithColor(word)) {
      return word;
    }
    return carry + word;
  }

  private static boolean startsWithColor(String word) {
    return word.length() >= 2 && word.charAt(0) == '&' && isColor(Character.toLowerCase(word.charAt(1)));
  }

  private static boolean isColor(char code) {
    return (code >= '0' && code <= '9') || (code >= 'a' && code <= 'f') || code == 'r';
  }

  static boolean isLegacyCode(char code) {
    return isColor(code) || code == 'k' || code == 'l' || code == 'm' || code == 'n' || code == 'o';
  }

  public sealed interface SplitResult permits Parts, TooLong, WordTooLong {
  }

  public record Parts(List<String> parts) implements SplitResult {
  }

  public record TooLong() implements SplitResult {
  }

  public record WordTooLong() implements SplitResult {
  }
}
