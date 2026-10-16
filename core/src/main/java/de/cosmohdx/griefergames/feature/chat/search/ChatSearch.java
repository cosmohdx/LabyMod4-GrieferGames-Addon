package de.cosmohdx.griefergames.feature.chat.search;

import de.cosmohdx.griefergames.GrieferGames;
import de.cosmohdx.griefergames.feature.chat.GGChatProcessEvent;
import java.io.IOException;
import java.util.UUID;
import net.labymod.api.client.chat.ChatMessage;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.serializer.gson.GsonComponentSerializer;
import net.labymod.api.event.Phase;
import net.labymod.api.event.Priority;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.chat.ChatReceiveEvent;
import net.labymod.api.event.client.lifecycle.GameTickEvent;
import net.labymod.api.event.client.lifecycle.ShutdownEvent;

/**
 * Records displayed chat lines after the addon's own chat changes and keeps the session file.
 */
public final class ChatSearch {

  private static final long MAINTENANCE_INTERVAL_MILLIS = 3L * 60L * 1000L;

  private final GrieferGames addon;
  private final ChatSearchLog log;
  private UUID lastProcessedMessageId;
  private long lastMaintenanceMillis;

  public ChatSearch(GrieferGames addon) {
    this(addon, new ChatSearchLog(ChatSearchLog.defaultDirectory()));
  }

  ChatSearch(GrieferGames addon, ChatSearchLog log) {
    this.addon = addon;
    this.log = log;
  }

  public void start() {
    try {
      this.log.open();
      this.lastMaintenanceMillis = System.currentTimeMillis();
      this.maintain();
    } catch (IOException exception) {
      this.addon.logger().error(GrieferGames.LOG_PREFIX + "Failed to open chat search log", exception);
    }
  }

  public void deleteNow() {
    this.log.deleteNow();
  }

  public java.util.List<ChatSearchEntry> entries() {
    return this.log.snapshot();
  }

  /** Compacts the session log using the current retention settings. */
  public void maintainNow() {
    this.maintain();
  }

  @Subscribe(Priority.LATEST)
  public void onProcessed(GGChatProcessEvent event) {
    ChatMessage message = event.getMessage();
    if (message != null) {
      this.lastProcessedMessageId = message.messageId();
    }
    if (message == null || event.isCancelled() || !this.allowsRecording()) {
      return;
    }
    if (event.isSecondChat() && event.isKeepInRegularChat()) {
      this.record(ChatSearchTab.MAIN, message);
      this.record(ChatSearchTab.SECOND, message);
    } else if (event.isSecondChat()) {
      this.record(ChatSearchTab.SECOND, message);
    } else {
      this.record(ChatSearchTab.MAIN, message);
    }
  }

  /**
   * Covers chat that never went through the GrieferGames processing event, for example while
   * the "only on GrieferGames" option is off and the player is on another server.
   */
  @Subscribe(Priority.LATEST)
  public void onReceive(ChatReceiveEvent event) {
    ChatMessage message = event.chatMessage();
    if (message == null || event.isCancelled() || !this.allowsRecording()) {
      return;
    }
    if (message.messageId() != null && message.messageId().equals(this.lastProcessedMessageId)) {
      return;
    }
    this.record(ChatSearchTab.MAIN, message);
  }

  @Subscribe
  public void onTick(GameTickEvent event) {
    if (event.phase() != Phase.POST) {
      return;
    }
    long now = System.currentTimeMillis();
    if (now - this.lastMaintenanceMillis < MAINTENANCE_INTERVAL_MILLIS) {
      return;
    }
    this.lastMaintenanceMillis = now;
    this.maintain();
  }

  @Subscribe
  public void onShutdown(ShutdownEvent event) {
    if (event.stage() == ShutdownEvent.Stage.PRE || event.stage() == ShutdownEvent.Stage.CRASH) {
      this.close();
    }
  }

  public void close() {
    this.log.shutdown();
  }

  private boolean allowsRecording() {
    ChatSearchConfig search = this.addon.configuration().chat().chatSearch();
    return ChatSearchGate.shouldRecord(
        this.addon.configuration().enabled().get(),
        this.addon.configuration().chat().isEnabled(),
        search.isEnabled(),
        search.onlyOnGrieferGames(),
        this.addon.state().isOnGrieferGames(),
        true
    );
  }

  private void record(ChatSearchTab tab, ChatMessage message) {
    Component component = message.component() == null ? Component.empty() : message.component();
    String json;
    try {
      json = GsonComponentSerializer.gson().serialize(component);
    } catch (RuntimeException exception) {
      json = "";
    }
    String plain = message.getPlainText() == null ? "" : message.getPlainText();
    String subServer = this.addon.state().getSubServer();
    this.log.append(new ChatSearchEntry(System.currentTimeMillis(), tab, subServer, json, plain));
  }

  private void maintain() {
    ChatSearchConfig search = this.addon.configuration().chat().chatSearch();
    this.log.submitRetention(
        System.currentTimeMillis(),
        search.retentionMinutes() * 60_000L,
        search.maxLines()
    );
  }
}
