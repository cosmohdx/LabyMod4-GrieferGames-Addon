package de.cosmohdx.griefergames.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.Test;

class OutgoingMessageQueueTest {

  @Test
  void partsWaitForTheGapAndALateTickDoesNotSendThemTogether() {
    AtomicLong now = new AtomicLong();
    List<String> sent = new ArrayList<>();
    OutgoingMessageQueue queue = queue(sent, now);

    queue.enqueue(List.of("a", "b"), 3_000L, message -> false);
    queue.tick(0L);
    assertTrue(sent.isEmpty());

    queue.tick(3_000L);
    assertEquals(List.of("a"), sent);
    queue.tick(3_000L);
    assertEquals(List.of("a"), sent);
    queue.tick(6_000L);
    assertEquals(List.of("a", "b"), sent);

    sent.clear();
    queue.enqueue(List.of("c", "d"), 3_000L, message -> false);
    queue.tick(10_000L);
    assertEquals(List.of("c"), sent);
    queue.tick(10_000L);
    assertEquals(List.of("c"), sent);
    queue.tick(13_000L);
    assertEquals(List.of("c", "d"), sent);
  }

  @Test
  void abortDropsTheRestAndClearEmptiesTheQueue() {
    AtomicLong now = new AtomicLong();
    List<String> sent = new ArrayList<>();
    AtomicInteger dropped = new AtomicInteger();
    OutgoingMessageQueue queue = queue(sent, now);
    queue.setDroppedListener(dropped::addAndGet);

    queue.enqueue(List.of("a", "b"), 1_000L, message -> true);
    queue.tick(5_000L);
    assertTrue(sent.isEmpty());
    assertEquals(2, dropped.get());
    assertFalse(queue.hasPending());

    queue.enqueue(List.of("c", "d"), 1_000L, message -> false);
    assertEquals(2, queue.clear());
    assertEquals(4, dropped.get());
    queue.tick(99_000L);
    assertTrue(sent.isEmpty());
    assertEquals(0, queue.clear());
    assertEquals(4, dropped.get());
  }

  @Test
  void clearResetsTheGapForTheNextMessage() {
    AtomicLong now = new AtomicLong();
    List<String> sent = new ArrayList<>();
    OutgoingMessageQueue queue = queue(sent, now);

    queue.enqueue(List.of("a"), 3_000L, message -> false);
    queue.clear();
    queue.enqueue(List.of("b"), 3_000L, message -> false);
    queue.tick(3_000L);
    assertEquals(List.of("b"), sent);
  }

  @Test
  void dispatchMarksTheLineInFlightUntilSendReturns() {
    AtomicLong now = new AtomicLong();
    List<String> sent = new ArrayList<>();
    OutgoingMessageQueue[] queue = new OutgoingMessageQueue[1];
    queue[0] = new OutgoingMessageQueue(message -> {
      assertTrue(queue[0].claimFollowUp(message));
      sent.add(message);
    }, now::get);

    queue[0].dispatch("hallo");
    assertEquals(List.of("hallo"), sent);
    assertFalse(queue[0].claimFollowUp("hallo"));
  }

  @Test
  void aFollowUpCanBeClaimedOnceAfterSendReturns() {
    AtomicLong now = new AtomicLong();
    OutgoingMessageQueue queue = queue(new ArrayList<>(), now);
    queue.dispatch("teil");
    assertTrue(queue.claimFollowUp("teil"));
    assertFalse(queue.claimFollowUp("teil"));

    queue.dispatch("spaeter");
    now.set(2_001L);
    assertFalse(queue.claimFollowUp("spaeter"));
  }

  private static OutgoingMessageQueue queue(List<String> sent, AtomicLong now) {
    return new OutgoingMessageQueue(sent::add, now::get);
  }
}
