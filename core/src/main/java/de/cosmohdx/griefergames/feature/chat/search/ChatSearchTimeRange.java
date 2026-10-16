package de.cosmohdx.griefergames.feature.chat.search;

/**
 * Relative time window for the search UI. {@link #ALL} keeps every retained entry.
 */
public enum ChatSearchTimeRange {
  ALL(0L),
  MINUTES_5(5L * 60_000L),
  MINUTES_15(15L * 60_000L),
  MINUTES_30(30L * 60_000L),
  HOURS_1(60L * 60_000L),
  HOURS_6(6L * 60L * 60_000L),
  HOURS_24(24L * 60L * 60_000L);

  private final long windowMillis;

  ChatSearchTimeRange(long windowMillis) {
    this.windowMillis = windowMillis;
  }

  /**
   * Earliest timestamp to include, or {@code 0} when the full retained history is shown.
   */
  public long minTimestamp(long nowMillis) {
    if (this.windowMillis <= 0L) {
      return 0L;
    }
    return Math.max(0L, nowMillis - this.windowMillis);
  }
}
