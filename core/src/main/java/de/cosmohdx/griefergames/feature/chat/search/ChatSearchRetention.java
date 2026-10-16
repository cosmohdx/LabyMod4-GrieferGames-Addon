package de.cosmohdx.griefergames.feature.chat.search;

import java.util.ArrayList;
import java.util.List;

/**
 * Drops entries that are older than the retention window or beyond the line cap.
 * The newest entries are kept.
 */
public final class ChatSearchRetention {

  public static final int MIN_MINUTES = 5;
  public static final int MAX_MINUTES = 24 * 60;
  public static final int DEFAULT_MINUTES = 30;
  public static final int DEFAULT_MAX_LINES = 10_000;

  private ChatSearchRetention() {
  }

  public static int clampMinutes(int minutes) {
    return Math.max(MIN_MINUTES, Math.min(MAX_MINUTES, minutes));
  }

  public static int clampMaxLines(int maxLines) {
    return Math.max(1, maxLines);
  }

  public static List<ChatSearchEntry> prune(
      List<ChatSearchEntry> entries,
      long nowMillis,
      long retentionMillis,
      int maxLines) {
    if (entries == null || entries.isEmpty()) {
      return List.of();
    }
    long cutoff = nowMillis - Math.max(0L, retentionMillis);
    int limit = clampMaxLines(maxLines);
    List<ChatSearchEntry> kept = new ArrayList<>(Math.min(entries.size(), limit));
    for (ChatSearchEntry entry : entries) {
      if (entry == null) {
        continue;
      }
      if (entry.timestamp() >= cutoff) {
        kept.add(entry);
      }
    }
    if (kept.size() > limit) {
      return List.copyOf(kept.subList(kept.size() - limit, kept.size()));
    }
    return List.copyOf(kept);
  }
}
