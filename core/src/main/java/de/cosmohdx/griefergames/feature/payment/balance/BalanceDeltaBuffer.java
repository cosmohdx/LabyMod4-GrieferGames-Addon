package de.cosmohdx.griefergames.feature.payment.balance;

import java.math.BigDecimal;
import java.util.ArrayDeque;
import java.util.List;

/**
 * Newest cash deltas, capped by count and age, so a change that arrives before the
 * matching chat line can still be paired with it.
 */
public final class BalanceDeltaBuffer {

  public static final int DEFAULT_CAPACITY = 10;
  public static final long DEFAULT_TTL_MILLIS = 10_000L;

  private final int capacity;
  private final long ttlMillis;
  private final Object lock = new Object();
  private final ArrayDeque<Entry> entries = new ArrayDeque<>();

  public BalanceDeltaBuffer() {
    this(DEFAULT_CAPACITY, DEFAULT_TTL_MILLIS);
  }

  public BalanceDeltaBuffer(int capacity, long ttlMillis) {
    if (capacity < 1) {
      throw new IllegalArgumentException("capacity must be at least 1");
    }
    if (ttlMillis < 1) {
      throw new IllegalArgumentException("ttl must be at least 1 ms");
    }
    this.capacity = capacity;
    this.ttlMillis = ttlMillis;
  }

  public void add(BigDecimal delta, long timestamp) {
    synchronized (this.lock) {
      this.evict(timestamp);
      while (this.entries.size() >= this.capacity) {
        this.entries.removeFirst();
      }
      this.entries.addLast(new Entry(delta, timestamp));
    }
  }

  public List<Entry> entries(long now) {
    synchronized (this.lock) {
      this.evict(now);
      return List.copyOf(this.entries);
    }
  }

  public void clear() {
    synchronized (this.lock) {
      this.entries.clear();
    }
  }

  private void evict(long now) {
    while (!this.entries.isEmpty()
        && now - this.entries.peekFirst().timestamp() >= this.ttlMillis) {
      this.entries.removeFirst();
    }
  }

  public record Entry(BigDecimal delta, long timestamp) {
  }
}
