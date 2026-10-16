package de.cosmohdx.griefergames.feature.chat.search;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Compiled search query. An empty query matches every line and produces no highlights.
 */
public final class ChatSearchQuery {

  private final String raw;
  private final boolean regex;
  private final Pattern pattern;
  private final boolean invalid;

  private ChatSearchQuery(String raw, boolean regex, Pattern pattern, boolean invalid) {
    this.raw = raw == null ? "" : raw;
    this.regex = regex;
    this.pattern = pattern;
    this.invalid = invalid;
  }

  public static ChatSearchQuery compile(String raw, boolean regex) {
    String text = raw == null ? "" : raw;
    if (text.isEmpty()) {
      return new ChatSearchQuery("", regex, null, false);
    }
    try {
      int flags = regex ? 0 : Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.LITERAL;
      return new ChatSearchQuery(text, regex, Pattern.compile(text, flags), false);
    } catch (PatternSyntaxException ignored) {
      return new ChatSearchQuery(text, regex, null, true);
    }
  }

  public boolean isEmpty() {
    return this.raw.isEmpty();
  }

  public boolean isRegex() {
    return this.regex;
  }

  public boolean isInvalid() {
    return this.invalid;
  }

  public String raw() {
    return this.raw;
  }

  Pattern pattern() {
    return this.pattern;
  }
}
