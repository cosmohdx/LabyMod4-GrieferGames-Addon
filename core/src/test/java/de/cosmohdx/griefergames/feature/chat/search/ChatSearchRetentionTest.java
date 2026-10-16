package de.cosmohdx.griefergames.feature.chat.search;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class ChatSearchRetentionTest {

  @Test
  void dropsEntriesOutsideTheRetentionWindow() {
    long now = 1_000_000L;
    List<ChatSearchEntry> entries = List.of(
        entry(now - 40 * 60_000L, "old"),
        entry(now - 10 * 60_000L, "kept")
    );

    List<ChatSearchEntry> kept = ChatSearchRetention.prune(entries, now, 30 * 60_000L, 10_000);

    assertEquals(1, kept.size());
    assertEquals("kept", kept.get(0).plainText());
  }

  @Test
  void keepsOnlyTheNewestLinesWhenTheCapIsReached() {
    List<ChatSearchEntry> entries = new ArrayList<>();
    for (int index = 0; index < 5; index++) {
      entries.add(entry(1_000L + index, "line-" + index));
    }

    List<ChatSearchEntry> kept = ChatSearchRetention.prune(entries, 10_000L, 60_000L, 3);

    assertEquals(3, kept.size());
    assertEquals("line-2", kept.get(0).plainText());
    assertEquals("line-4", kept.get(2).plainText());
  }

  @Test
  void clampsConfiguredMinutes() {
    assertEquals(5, ChatSearchRetention.clampMinutes(1));
    assertEquals(1440, ChatSearchRetention.clampMinutes(10_000));
    assertEquals(30, ChatSearchRetention.clampMinutes(30));
  }

  @Test
  void emptyInputStaysEmpty() {
    assertTrue(ChatSearchRetention.prune(List.of(), 1L, 1L, 10).isEmpty());
  }

  private static ChatSearchEntry entry(long timestamp, String text) {
    return new ChatSearchEntry(timestamp, ChatSearchTab.MAIN, "cb1", "{\"text\":\"" + text + "\"}", text);
  }
}
