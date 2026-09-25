package de.cosmohdx.griefergames.feature.payment.balance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.cosmohdx.griefergames.feature.payment.balance.BalanceChangedEvent.Account;
import de.cosmohdx.griefergames.payload.model.AccountBalancePayload;
import de.cosmohdx.griefergames.payload.model.BankBalancePayload;
import de.cosmohdx.griefergames.payload.model.MysteryModBankPayload;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.Test;

class BalanceTrackerTest {

  private final AtomicLong clock = new AtomicLong(1_000L);
  private final List<BalanceChangedEvent> events = new ArrayList<>();
  private final BalanceTracker tracker = this.newTracker();

  @Test
  void firstValueIsInitialAndNotADelta() {
    this.tracker.onAccountBalance(new AccountBalancePayload(12.5));

    assertTrue(this.tracker.cashKnown());
    assertFalse(this.tracker.bankKnown());
    assertEquals(new BigDecimal("12.50"), this.tracker.cash());
    assertEquals(1, this.events.size());
    BalanceChangedEvent event = this.events.get(0);
    assertEquals(Account.CASH, event.account());
    assertTrue(event.initial());
    assertEquals(new BigDecimal("0.00"), event.oldValue());
    assertEquals(new BigDecimal("12.50"), event.newValue());
    assertEquals(new BigDecimal("0.00"), event.delta());
    assertEquals(1_000L, event.timestamp());
    assertTrue(this.tracker.cashDeltas().isEmpty());
  }

  @Test
  void roundsHalfUpToTwoDecimals() {
    this.tracker.onAccountBalance(new AccountBalancePayload(1.005));
    assertEquals(new BigDecimal("1.01"), this.tracker.cash());

    this.clock.set(2_000L);
    this.tracker.onAccountBalance(new AccountBalancePayload(1.004));

    assertEquals(new BigDecimal("1.00"), this.tracker.cash());
    BalanceChangedEvent change = this.events.get(this.events.size() - 1);
    assertFalse(change.initial());
    assertEquals(new BigDecimal("-0.01"), change.delta());
    assertEquals(new BigDecimal("-0.01"), this.tracker.cashDeltas().get(0).delta());
  }

  @Test
  void sameValueDoesNotCreateADelta() {
    this.tracker.onAccountBalance(new AccountBalancePayload(5));
    this.events.clear();
    long changedAt = this.tracker.lastCashChangeAt();

    this.clock.set(4_000L);
    this.tracker.onAccountBalance(new AccountBalancePayload(5));

    assertTrue(this.events.isEmpty());
    assertTrue(this.tracker.cashDeltas().isEmpty());
    assertEquals(changedAt, this.tracker.lastCashChangeAt());
  }

  @Test
  void disconnectResetsAndTheNextValueIsInitial() {
    this.tracker.onAccountBalance(new AccountBalancePayload(10));
    this.clock.set(2_000L);
    this.tracker.onAccountBalance(new AccountBalancePayload(12));
    this.tracker.onBankBalance(new BankBalancePayload(3));

    this.tracker.onServerDisconnect(null);

    assertFalse(this.tracker.cashKnown());
    assertFalse(this.tracker.bankKnown());
    assertEquals(new BigDecimal("0.00"), this.tracker.cash());
    assertEquals(new BigDecimal("0.00"), this.tracker.bank());
    assertEquals(0L, this.tracker.lastCashChangeAt());
    assertTrue(this.tracker.cashDeltas().isEmpty());

    this.events.clear();
    this.clock.set(3_000L);
    this.tracker.onAccountBalance(new AccountBalancePayload(12));

    assertEquals(1, this.events.size());
    assertTrue(this.events.get(0).initial());
    assertEquals(new BigDecimal("0.00"), this.events.get(0).delta());
    assertTrue(this.tracker.cashDeltas().isEmpty());
    assertEquals(3_000L, this.tracker.lastCashChangeAt());
  }

  @Test
  void bankAndCashStaySeparate() {
    this.clock.set(5L);
    this.tracker.onAccountBalance(new AccountBalancePayload(10));
    this.tracker.onBankBalance(new BankBalancePayload(4));
    this.clock.set(6L);
    this.tracker.onAccountBalance(new AccountBalancePayload(11.5));
    this.tracker.onMysteryModBank(new MysteryModBankPayload(new BigDecimal("4.00")));

    assertEquals(new BigDecimal("11.50"), this.tracker.cash());
    assertEquals(new BigDecimal("4.00"), this.tracker.bank());
    assertEquals(1, this.tracker.cashDeltas().size());
    assertEquals(new BigDecimal("1.50"), this.tracker.cashDeltas().get(0).delta());
    assertEquals(3, this.events.size());
    assertTrue(this.events.get(0).initial());
    assertEquals(Account.CASH, this.events.get(0).account());
    assertEquals(Account.BANK, this.events.get(1).account());
    assertTrue(this.events.get(1).initial());
    assertEquals(Account.CASH, this.events.get(2).account());
    assertEquals(new BigDecimal("1.50"), this.events.get(2).delta());
    assertEquals(6L, this.tracker.lastCashChangeAt());
  }

  @Test
  void mysteryModBankInitialisesTheBankBalance() {
    this.tracker.onMysteryModBank(new MysteryModBankPayload(new BigDecimal("1500")));

    assertFalse(this.tracker.cashKnown());
    assertTrue(this.tracker.bankKnown());
    assertEquals(new BigDecimal("1500.00"), this.tracker.bank());
    assertEquals(Account.BANK, this.events.get(0).account());
    assertTrue(this.events.get(0).initial());
    assertTrue(this.tracker.cashDeltas().isEmpty());
  }

  @Test
  void cashBufferDropsDeltasOlderThanTenSeconds() {
    this.clock.set(0L);
    this.tracker.onAccountBalance(new AccountBalancePayload(10));
    this.clock.set(1_000L);
    this.tracker.onAccountBalance(new AccountBalancePayload(12));
    this.clock.set(12_000L);
    this.tracker.onAccountBalance(new AccountBalancePayload(15));

    assertEquals(1, this.tracker.cashDeltas().size());
    assertEquals(new BigDecimal("3.00"), this.tracker.cashDeltas().get(0).delta());
  }

  @Test
  void nonFinitePayloadIsIgnored() {
    this.tracker.onAccountBalance(new AccountBalancePayload(Double.NaN));
    this.tracker.onBankBalance(new BankBalancePayload(Double.POSITIVE_INFINITY));

    assertFalse(this.tracker.cashKnown());
    assertFalse(this.tracker.bankKnown());
    assertTrue(this.events.isEmpty());
  }

  @Test
  void updatesFromSeveralThreadsStayScaled() throws Exception {
    BalanceTracker concurrent = new BalanceTracker(() -> 0L, event -> {
    });
    int threads = 4;
    ExecutorService pool = Executors.newFixedThreadPool(threads);
    CountDownLatch ready = new CountDownLatch(threads);
    CountDownLatch start = new CountDownLatch(1);
    CountDownLatch done = new CountDownLatch(threads);
    for (int thread = 0; thread < threads; thread++) {
      int id = thread;
      pool.execute(() -> {
        ready.countDown();
        try {
          if (!start.await(5, TimeUnit.SECONDS)) {
            return;
          }
          for (int step = 0; step < 100; step++) {
            concurrent.onAccountBalance(new AccountBalancePayload(id + step / 100.0));
            concurrent.onBankBalance(new BankBalancePayload(step));
          }
        } catch (InterruptedException exception) {
          Thread.currentThread().interrupt();
        } finally {
          done.countDown();
        }
      });
    }
    assertTrue(ready.await(5, TimeUnit.SECONDS));
    start.countDown();
    assertTrue(done.await(5, TimeUnit.SECONDS));
    pool.shutdownNow();
    assertEquals(2, concurrent.cash().scale());
    assertEquals(2, concurrent.bank().scale());
    assertTrue(concurrent.cashKnown());
    assertTrue(concurrent.bankKnown());
  }

  private BalanceTracker newTracker() {
    BalanceTracker created = new BalanceTracker(this.clock::get, null);
    created.addListener(this.events::add);
    return created;
  }
}
