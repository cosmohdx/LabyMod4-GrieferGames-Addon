package de.cosmohdx.griefergames.feature.chat.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;

/**
 * Decides which recorded lines match the current filters and where the query occurs.
 */
public final class ChatSearchMatcher {

  private ChatSearchMatcher() {
  }

  public static boolean accepts(
      ChatSearchEntry entry,
      ChatSearchTabFilter tab,
      long minTimestamp,
      ChatSearchQuery query) {
    if (entry == null) {
      return false;
    }
    if (tab != null && !tab.accepts(entry.tab())) {
      return false;
    }
    if (entry.timestamp() < minTimestamp) {
      return false;
    }
    if (query == null || query.isEmpty()) {
      return true;
    }
    if (query.isInvalid() || query.pattern() == null) {
      return false;
    }
    return query.pattern().matcher(entry.plainText()).find();
  }

  public static List<ChatSearchMatch> matches(String plainText, ChatSearchQuery query) {
    if (plainText == null || plainText.isEmpty() || query == null || query.isEmpty()
        || query.isInvalid() || query.pattern() == null) {
      return List.of();
    }
    Matcher matcher = query.pattern().matcher(plainText);
    List<ChatSearchMatch> matches = new ArrayList<>();
    int guard = 0;
    while (matcher.find()) {
      int start = matcher.start();
      int end = matcher.end();
      if (end <= start) {
        if (start >= plainText.length()) {
          break;
        }
        end = start + 1;
        matcher.region(end, plainText.length());
      }
      matches.add(new ChatSearchMatch(start, end));
      if (++guard > plainText.length()) {
        break;
      }
    }
    return Collections.unmodifiableList(matches);
  }
}
