package de.cosmohdx.griefergames.feature.payment.balance;

import java.math.BigDecimal;
import net.labymod.api.event.Event;

/**
 * Cash or bank balance after a payload was applied.
 *
 * <p>The first value after joining, and the first value after a disconnect, has
 * {@link #initial()} set and a zero {@link #delta()}. Later updates carry the difference
 * between {@link #oldValue()} and {@link #newValue()}.
 */
public final class BalanceChangedEvent implements Event {

  public enum Account {
    CASH,
    BANK
  }

  private final Account account;
  private final BigDecimal oldValue;
  private final BigDecimal newValue;
  private final BigDecimal delta;
  private final boolean initial;
  private final long timestamp;

  public BalanceChangedEvent(
      Account account,
      BigDecimal oldValue,
      BigDecimal newValue,
      BigDecimal delta,
      boolean initial,
      long timestamp
  ) {
    this.account = account;
    this.oldValue = oldValue;
    this.newValue = newValue;
    this.delta = delta;
    this.initial = initial;
    this.timestamp = timestamp;
  }

  public Account account() {
    return this.account;
  }

  public BigDecimal oldValue() {
    return this.oldValue;
  }

  public BigDecimal newValue() {
    return this.newValue;
  }

  public BigDecimal delta() {
    return this.delta;
  }

  public boolean initial() {
    return this.initial;
  }

  public long timestamp() {
    return this.timestamp;
  }
}
