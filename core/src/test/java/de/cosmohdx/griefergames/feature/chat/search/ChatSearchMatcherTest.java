package de.cosmohdx.griefergames.feature.chat.search;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class ChatSearchMatcherTest {

  @Test
  void emptyQueryKeepsEveryLineWithoutHighlights() {
    ChatSearchEntry entry = entry(ChatSearchTab.MAIN, "Hallo Welt");
    ChatSearchQuery query = ChatSearchQuery.compile("", false);

    assertTrue(ChatSearchMatcher.accepts(entry, ChatSearchTabFilter.ALL, 0L, query));
    assertTrue(ChatSearchMatcher.matches(entry.plainText(), query).isEmpty());
  }

  @Test
  void literalSearchIsCaseInsensitiveAndReportsRanges() {
    ChatSearchEntry entry = entry(ChatSearchTab.MAIN, "Hallo Notch");
    ChatSearchQuery query = ChatSearchQuery.compile("notch", false);

    assertTrue(ChatSearchMatcher.accepts(entry, ChatSearchTabFilter.MAIN, 0L, query));
    List<ChatSearchMatch> matches = ChatSearchMatcher.matches(entry.plainText(), query);
    assertEquals(1, matches.size());
    assertEquals(6, matches.get(0).start());
    assertEquals(11, matches.get(0).end());
  }

  @Test
  void regexSearchAndTabFilterWorkTogether() {
    ChatSearchEntry main = entry(ChatSearchTab.MAIN, "Spieler A: hi");
    ChatSearchEntry second = entry(ChatSearchTab.SECOND, "Spieler B: hi");
    ChatSearchQuery query = ChatSearchQuery.compile("Spieler [AB]", true);

    assertTrue(ChatSearchMatcher.accepts(main, ChatSearchTabFilter.ALL, 0L, query));
    assertFalse(ChatSearchMatcher.accepts(second, ChatSearchTabFilter.MAIN, 0L, query));
    assertTrue(ChatSearchMatcher.accepts(second, ChatSearchTabFilter.SECOND, 0L, query));
  }

  @Test
  void invalidRegexMatchesNothing() {
    ChatSearchQuery query = ChatSearchQuery.compile("(", true);
    assertTrue(query.isInvalid());
    assertFalse(ChatSearchMatcher.accepts(entry(ChatSearchTab.MAIN, "text"), ChatSearchTabFilter.ALL, 0L, query));
  }

  @Test
  void timeFilterDropsOlderLines() {
    ChatSearchEntry old = new ChatSearchEntry(100L, ChatSearchTab.MAIN, "", "{\"text\":\"a\"}", "a");
    ChatSearchEntry recent = new ChatSearchEntry(1_000L, ChatSearchTab.MAIN, "", "{\"text\":\"b\"}", "b");
    ChatSearchQuery query = ChatSearchQuery.compile("", false);

    assertFalse(ChatSearchMatcher.accepts(old, ChatSearchTabFilter.ALL, 500L, query));
    assertTrue(ChatSearchMatcher.accepts(recent, ChatSearchTabFilter.ALL, 500L, query));
  }

  private static ChatSearchEntry entry(ChatSearchTab tab, String text) {
    return new ChatSearchEntry(1L, tab, "cb1", "{\"text\":\"" + text + "\"}", text);
  }
}
