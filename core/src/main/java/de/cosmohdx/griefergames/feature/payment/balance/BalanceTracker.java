package de.cosmohdx.griefergames.feature.payment.balance;

import de.cosmohdx.griefergames.GrieferGames;
import de.cosmohdx.griefergames.feature.payment.balance.BalanceChangedEvent.Account;
import de.cosmohdx.griefergames.payload.model.AccountBalancePayload;
import de.cosmohdx.griefergames.payload.model.BankBalancePayload;
import de.cosmohdx.griefergames.payload.model.MysteryModBankPayload;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.LongSupplier;
import net.labymod.api.Laby;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.network.server.ServerDisconnectEvent;

/**
 * Remembers the cash and bank balances from the server payloads.
 *
 * <p>The first value after startup, and the first value after {@link ServerDisconnectEvent},
 * only stores the current amount. Later values publish the difference. State is guarded
 * so a payload thread and the HUD thread can use it at the same time.
 */
public class BalanceTracker {

  private final LongSupplier clock;
  private final Consumer<BalanceChangedEvent> publisher;
  private final Consumer<String> debug;
  private final List<Consumer<BalanceChangedEvent>> listeners = new CopyOnWriteArrayList<>();
  private final BalanceDeltaBuffer cashDeltas = new BalanceDeltaBuffer();
  private final Object lock = new Object();

  private BigDecimal cash = MoneyFormat.ZERO;
  private BigDecimal bank = MoneyFormat.ZERO;
  private boolean cashKnown;
  private boolean bankKnown;
  private long lastCashChangeAt;

  public BalanceTracker(GrieferGames griefergames) {
    this(System::currentTimeMillis, Laby::fireEvent, message -> {
      if (griefergames.configuration().payment().logBalanceTiming()) {
        griefergames.logger().info(GrieferGames.LOG_PREFIX + message);
      }
    });
    griefergames.payloads().subscribe(AccountBalancePayload.class, this::onAccountBalance);
    griefergames.payloads().subscribe(BankBalancePayload.class, this::onBankBalance);
    griefergames.payloads().subscribe(MysteryModBankPayload.class, this::onMysteryModBank);
  }

  BalanceTracker(LongSupplier clock, Consumer<BalanceChangedEvent> publisher) {
    this(clock, publisher, message -> {
    });
  }

  BalanceTracker(
      LongSupplier clock,
      Consumer<BalanceChangedEvent> publisher,
      Consumer<String> debug
  ) {
    this.clock = clock;
    this.publisher = publisher;
    this.debug = debug;
  }

  public void addListener(Consumer<BalanceChangedEvent> listener) {
    this.listeners.add(listener);
  }

  void onAccountBalance(AccountBalancePayload payload) {
    this.applyDouble(Account.CASH, payload.balance(), AccountBalancePayload.ID);
  }

  void onBankBalance(BankBalancePayload payload) {
    this.applyDouble(Account.BANK, payload.balance(), BankBalancePayload.ID);
  }

  void onMysteryModBank(MysteryModBankPayload payload) {
    this.apply(Account.BANK, MoneyFormat.money(payload.amount()), MysteryModBankPayload.ID);
  }

  @Subscribe
  public void onServerDisconnect(ServerDisconnectEvent event) {
    this.reset();
  }

  public void reset() {
    synchronized (this.lock) {
      this.cash = MoneyFormat.ZERO;
      this.bank = MoneyFormat.ZERO;
      this.cashKnown = false;
      this.bankKnown = false;
      this.lastCashChangeAt = 0L;
      this.cashDeltas.clear();
    }
  }

  public BigDecimal cash() {
    synchronized (this.lock) {
      return this.cash;
    }
  }

  public BigDecimal bank() {
    synchronized (this.lock) {
      return this.bank;
    }
  }

  public boolean cashKnown() {
    synchronized (this.lock) {
      return this.cashKnown;
    }
  }

  public boolean bankKnown() {
    synchronized (this.lock) {
      return this.bankKnown;
    }
  }

  public long lastCashChangeAt() {
    synchronized (this.lock) {
      return this.lastCashChangeAt;
    }
  }

  public List<BalanceDeltaBuffer.Entry> cashDeltas() {
    synchronized (this.lock) {
      return this.cashDeltas.entries(this.clock.getAsLong());
    }
  }

  private void applyDouble(Account account, double raw, String source) {
    if (!Double.isFinite(raw)) {
      this.debug.accept(this.line(this.clock.getAsLong(), account, source) + " ignored=not-finite");
      return;
    }
    this.apply(account, MoneyFormat.money(raw), source);
  }

  private void apply(Account account, BigDecimal value, String source) {
    BalanceChangedEvent event = null;
    String logLine;
    synchronized (this.lock) {
      long timestamp = this.clock.getAsLong();
      boolean known = account == Account.CASH ? this.cashKnown : this.bankKnown;
      BigDecimal previous = account == Account.CASH ? this.cash : this.bank;
      if (known && previous.compareTo(value) == 0) {
        logLine = this.line(timestamp, account, source)
            + " initial=false unchanged=true value=" + value.toPlainString();
      } else {
        BigDecimal delta = known ? value.subtract(previous) : MoneyFormat.ZERO;
        if (account == Account.CASH) {
          this.cash = value;
          this.cashKnown = true;
          this.lastCashChangeAt = timestamp;
          if (known) {
            this.cashDeltas.add(delta, timestamp);
          }
        } else {
          this.bank = value;
          this.bankKnown = true;
        }
        event = new BalanceChangedEvent(account, previous, value, delta, !known, timestamp);
        logLine = this.line(timestamp, account, source)
            + " initial=" + event.initial()
            + " old=" + previous.toPlainString()
            + " new=" + value.toPlainString()
            + " delta=" + delta.toPlainString();
      }
    }
    this.debug.accept(logLine);
    if (event != null) {
      this.publish(event);
    }
  }

  private String line(long timestamp, Account account, String source) {
    String name = account == Account.CASH ? "cash" : "bank";
    return "balance t=" + timestamp
        + " n=" + System.nanoTime()
        + " account=" + name
        + " source=" + source;
  }

  private void publish(BalanceChangedEvent event) {
    for (Consumer<BalanceChangedEvent> listener : this.listeners) {
      listener.accept(event);
    }
    if (this.publisher != null) {
      this.publisher.accept(event);
    }
  }
}
