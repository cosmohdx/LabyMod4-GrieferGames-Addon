package de.cosmohdx.griefergames.core;

import de.cosmohdx.griefergames.feature.subserver.GGSubServerChangeEvent;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.LongSupplier;
import java.util.function.Predicate;
import net.labymod.api.event.Phase;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.lifecycle.GameTickEvent;
import net.labymod.api.event.client.network.server.ServerDisconnectEvent;

/**
 * Sends follow-up chat lines on the client thread, with a gap between them.
 *
 * <p>The tick is the same client-thread hook the AFK check already uses. Disconnect and a
 * sub-server change drop everything that has not been sent yet. A line currently being handed to
 * {@code sendMessage} is marked in-flight so a second pass through the send listener does not
 * colour or split it again.
 */
public final class OutgoingMessageQueue {

  private final Object lock = new Object();
  private final ArrayDeque<Scheduled> pending = new ArrayDeque<>();
  private final Consumer<String> sender;
  private final LongSupplier clock;
  private Consumer<Integer> droppedListener = count -> {
  };
  private static final long FOLLOW_UP_MEMORY_MS = 2_000L;

  private final ArrayDeque<Remembered> recent = new ArrayDeque<>();
  private long lastScheduledAt;
  private volatile String inFlight;

  public OutgoingMessageQueue(Consumer<String> sender) {
    this(sender, System::currentTimeMillis);
  }

  public OutgoingMessageQueue(Consumer<String> sender, LongSupplier clock) {
    this.sender = Objects.requireNonNull(sender);
    this.clock = Objects.requireNonNull(clock);
  }

  public void setDroppedListener(Consumer<Integer> droppedListener) {
    this.droppedListener = droppedListener == null ? count -> {
    } : droppedListener;
  }

  /**
   * Queues {@code parts} so each one waits {@code minDelayMs} after the previous send.
   * {@code abort} is checked before a part goes out; when it matches, that part and every
   * following aborted part are dropped.
   */
  public void enqueue(List<String> parts, long minDelayMs, Predicate<String> abort) {
    this.enqueue(parts, minDelayMs, abort, null);
  }

  public void enqueue(List<String> parts, long minDelayMs, Predicate<String> abort,
      Consumer<String> onDispatch) {
    if (parts == null || parts.isEmpty()) {
      return;
    }
    Predicate<String> stop = abort == null ? message -> false : abort;
    long delay = Math.max(0L, minDelayMs);
    synchronized (this.lock) {
      long cursor = Math.max(this.clock.getAsLong(), this.lastScheduledAt);
      for (String part : parts) {
        if (part == null || part.isEmpty()) {
          continue;
        }
        cursor += delay;
        this.pending.add(new Scheduled(part, cursor, delay, stop, onDispatch));
      }
      this.lastScheduledAt = cursor;
    }
  }

  public boolean hasPending() {
    synchronized (this.lock) {
      return !this.pending.isEmpty();
    }
  }

  /**
   * Drops every queued line. The dropped listener runs only when at least one line was waiting.
   */
  public int clear() {
    int dropped;
    synchronized (this.lock) {
      dropped = this.pending.size();
      this.pending.clear();
      this.lastScheduledAt = 0L;
    }
    if (dropped > 0) {
      this.droppedListener.accept(dropped);
    }
    return dropped;
  }

  public void dispatch(String message) {
    this.inFlight = message;
    synchronized (this.lock) {
      this.recent.add(new Remembered(message, this.clock.getAsLong() + FOLLOW_UP_MEMORY_MS));
    }
    try {
      this.sender.accept(message);
    } finally {
      this.inFlight = null;
    }
  }

  /**
   * True while this exact line is being handed to the sender, or once shortly afterwards if the
   * send event arrives on a later pass. A remembered line is only claimed once.
   */
  public boolean claimFollowUp(String message) {
    if (message == null) {
      return false;
    }
    if (message.equals(this.inFlight)) {
      this.forgetOne(message);
      return true;
    }
    return this.forgetOne(message);
  }

  private boolean forgetOne(String message) {
    long now = this.clock.getAsLong();
    synchronized (this.lock) {
      Iterator<Remembered> items = this.recent.iterator();
      while (items.hasNext()) {
        Remembered item = items.next();
        if (item.until < now) {
          items.remove();
          continue;
        }
        if (item.text.equals(message)) {
          items.remove();
          return true;
        }
      }
    }
    return false;
  }

  public void tick(long now) {
    String toSend = null;
    Consumer<String> onDispatch = null;
    int dropped = 0;
    synchronized (this.lock) {
      while (!this.pending.isEmpty() && this.pending.peek().abort.test(this.pending.peek().text)) {
        this.pending.remove();
        dropped++;
      }
      this.refreshLastScheduled();
      if (!this.pending.isEmpty() && this.pending.peek().sendAt <= now) {
        Scheduled next = this.pending.remove();
        toSend = next.text;
        onDispatch = next.onDispatch;
        this.respace(now);
      }
    }
    if (dropped > 0) {
      this.droppedListener.accept(dropped);
    }
    if (toSend == null) {
      return;
    }
    if (onDispatch != null) {
      onDispatch.accept(toSend);
    }
    this.dispatch(toSend);
  }

  @Subscribe
  public void onTick(GameTickEvent event) {
    if (event.phase() != Phase.POST) {
      return;
    }
    if (!this.hasPending()) {
      return;
    }
    this.tick(this.clock.getAsLong());
  }

  @Subscribe
  public void onServerQuit(ServerDisconnectEvent event) {
    this.clear();
  }

  @Subscribe
  public void onSubServerChange(GGSubServerChangeEvent event) {
    this.clear();
  }

  private void respace(long now) {
    long cursor = now;
    for (Scheduled item : this.pending) {
      long earliest = cursor + item.delayMs;
      if (item.sendAt < earliest) {
        item.sendAt = earliest;
      }
      cursor = item.sendAt;
    }
    this.refreshLastScheduled();
  }

  private void refreshLastScheduled() {
    this.lastScheduledAt = 0L;
    for (Scheduled item : this.pending) {
      if (item.sendAt > this.lastScheduledAt) {
        this.lastScheduledAt = item.sendAt;
      }
    }
  }

  private record Remembered(String text, long until) {
  }

  private static final class Scheduled {
    private final String text;
    private final long delayMs;
    private final Predicate<String> abort;
    private final Consumer<String> onDispatch;
    private long sendAt;

    private Scheduled(String text, long sendAt, long delayMs, Predicate<String> abort,
        Consumer<String> onDispatch) {
      this.text = text;
      this.sendAt = sendAt;
      this.delayMs = delayMs;
      this.abort = abort;
      this.onDispatch = onDispatch;
    }
  }
}
