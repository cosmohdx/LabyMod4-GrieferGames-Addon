package de.cosmohdx.griefergames.feature.payment.balance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class BalanceDeltaBufferTest {

  @Test
  void dropsEntriesOlderThanTenSeconds() {
    BalanceDeltaBuffer buffer = new BalanceDeltaBuffer();
    buffer.add(new BigDecimal("1.00"), 0L);
    buffer.add(new BigDecimal("2.00"), 10_000L);

    List<BalanceDeltaBuffer.Entry> entries = buffer.entries(10_000L);

    assertEquals(1, entries.size());
    assertEquals(new BigDecimal("2.00"), entries.get(0).delta());
    assertEquals(10_000L, entries.get(0).timestamp());
  }

  @Test
  void keepsAnEntryInsideTheWindow() {
    BalanceDeltaBuffer buffer = new BalanceDeltaBuffer();
    buffer.add(new BigDecimal("1.00"), 0L);
    buffer.add(new BigDecimal("2.00"), 9_999L);

    assertEquals(2, buffer.entries(9_999L).size());
  }

  @Test
  void keepsOnlyTheNewestTenEntries() {
    BalanceDeltaBuffer buffer = new BalanceDeltaBuffer(10, Long.MAX_VALUE);
    for (int index = 0; index < 11; index++) {
      buffer.add(BigDecimal.valueOf(index).setScale(2), index);
    }

    List<BalanceDeltaBuffer.Entry> entries = buffer.entries(10L);

    assertEquals(10, entries.size());
    assertEquals(new BigDecimal("1.00"), entries.get(0).delta());
    assertEquals(new BigDecimal("10.00"), entries.get(9).delta());
  }

  @Test
  void clearRemovesEveryEntry() {
    BalanceDeltaBuffer buffer = new BalanceDeltaBuffer();
    buffer.add(new BigDecimal("1.00"), 1L);
    buffer.clear();

    assertTrue(buffer.entries(1L).isEmpty());
  }
}
