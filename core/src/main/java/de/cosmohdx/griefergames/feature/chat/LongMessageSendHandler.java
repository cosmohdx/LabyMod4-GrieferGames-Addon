package de.cosmohdx.griefergames.feature.chat;

import de.cosmohdx.griefergames.GrieferGames;
import de.cosmohdx.griefergames.core.OutgoingMessageQueue;
import de.cosmohdx.griefergames.core.SubServerType;
import de.cosmohdx.griefergames.feature.chat.LongMessageSplitter.Parts;
import de.cosmohdx.griefergames.feature.chat.LongMessageSplitter.SplitResult;
import de.cosmohdx.griefergames.feature.subserver.GGNetworkTypeChangeEvent;
import de.cosmohdx.griefergames.feature.subserver.GGSubServerChangeEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.event.ClickEvent;
import net.labymod.api.client.component.event.HoverEvent;
import net.labymod.api.client.component.format.NamedTextColor;
import net.labymod.api.client.component.format.TextColor;
import net.labymod.api.client.component.format.Style;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.chat.ChatMessageSendEvent;
import net.labymod.api.event.client.network.server.ServerDisconnectEvent;
import net.labymod.api.util.I18n;

/**
 * Splits a long 1.8 chat line after the auto colour has been applied.
 * The first part leaves with the current send event. The rest waits on {@link OutgoingMessageQueue}.
 */
public final class LongMessageSendHandler {

  static final long MINIMUM_DELAY_MS = 2_500L;
  private static final Pattern CONTROL = Pattern.compile("^/gg-split (confirm|cancel) (\\d+)$");

  private final GrieferGames griefergames;
  private final OutgoingMessageQueue queue;
  private final LastConversationPartner partners;
  private PendingConfirm pending;
  private int nextToken = 1;

  public LongMessageSendHandler(GrieferGames griefergames, OutgoingMessageQueue queue,
      LastConversationPartner partners) {
    this.griefergames = griefergames;
    this.queue = queue;
    this.partners = partners;
    this.queue.setDroppedListener(count -> this.show("splitAborted"));
  }

  /**
   * @return {@code true} when the line was an addon confirm click and must not be sent
   */
  public boolean consumeControlMessage(ChatMessageSendEvent event, String message) {
    Matcher matcher = CONTROL.matcher(message == null ? "" : message);
    if (!matcher.matches()) {
      return false;
    }
    event.setCancelled(true);
    int token;
    try {
      token = Integer.parseInt(matcher.group(2));
    } catch (NumberFormatException exception) {
      return true;
    }
    if (this.pending == null || this.pending.token != token) {
      return true;
    }
    PendingConfirm confirmed = this.pending;
    this.pending = null;
    if ("confirm".equals(matcher.group(1))) {
      this.dispatch(confirmed.parts, confirmed.delayMs);
    }
    return true;
  }

  public void handleOutgoing(ChatMessageSendEvent event, String message) {
    if (event.isCancelled() || message == null || message.isEmpty()) {
      return;
    }
    if (!this.griefergames.state().isLegacyNetwork()) {
      return;
    }
    if (!this.griefergames.configuration().chat().splitLongMessages()) {
      return;
    }
    if (LongMessageSplitter.length(message) <= LongMessageSplitter.LEGACY_CHAT_LIMIT) {
      return;
    }
    Optional<String> text = OutgoingChat.textToSplit(
        message,
        this.griefergames.configuration().chat().splitPrivateMessages(),
        this.partners.name());
    if (text.isEmpty()) {
      return;
    }
    int maxParts = clampParts(this.griefergames.configuration().chat().splitMaxParts());
    SplitResult result = LongMessageSplitter.split(
        text.get(),
        LongMessageSplitter.LEGACY_CHAT_LIMIT,
        maxParts);
    if (!(result instanceof Parts parts)) {
      event.setCancelled(true);
      this.show("splitTooLong");
      return;
    }
    if (parts.parts().size() <= 1) {
      if (parts.parts().size() == 1 && !parts.parts().get(0).equals(message)) {
        event.changeMessage(parts.parts().get(0));
      }
      return;
    }
    long delay = delayMs(this.griefergames.configuration().chat().splitDelaySeconds());
    List<String> messages = parts.parts();
    if (this.griefergames.configuration().chat().splitConfirm()) {
      event.setCancelled(true);
      int token = this.nextToken++;
      this.pending = new PendingConfirm(token, messages, delay);
      this.showConfirm(messages.size(), token);
      return;
    }
    this.showSending(1, messages.size());
    event.changeMessage(messages.get(0));
    this.enqueueRest(messages, delay);
  }

  @Subscribe
  public void onChat(GGChatProcessEvent event) {
    if (!this.queue.hasPending() || event.getMessage() == null) {
      return;
    }
    if (SplitAbortMessages.aborts(event.getMessage().getPlainText())) {
      this.queue.clear();
    }
  }

  @Subscribe
  public void onSubServerChange(GGSubServerChangeEvent event) {
    this.dropPendingConfirm();
  }

  @Subscribe
  public void onNetwork(GGNetworkTypeChangeEvent event) {
    if (event.newType() != SubServerType.REGULAR) {
      this.dropPendingConfirm();
      this.partners.clear();
      this.queue.clear();
    }
  }

  @Subscribe
  public void onServerQuit(ServerDisconnectEvent event) {
    this.dropPendingConfirm();
    this.partners.clear();
  }

  private void dispatch(List<String> messages, long delay) {
    this.showSending(1, messages.size());
    this.queue.dispatch(messages.get(0));
    this.enqueueRest(messages, delay);
  }

  private void enqueueRest(List<String> messages, long delay) {
    if (messages.size() <= 1) {
      return;
    }
    List<String> rest = new ArrayList<>(messages.subList(1, messages.size()));
    int total = messages.size();
    int[] number = {2};
    this.queue.enqueue(rest, delay, part -> false, part -> {
      this.showSending(number[0], total);
      number[0]++;
    });
  }

  private void dropPendingConfirm() {
    this.pending = null;
  }

  private void showConfirm(int parts, int token) {
    String prompt = this.translate("splitConfirmPrompt").replace("{parts}", Integer.toString(parts));
    Component message = Component.empty()
        .append(Component.text(prompt))
        .append(Component.text(" "))
        .append(this.action(this.translate("splitConfirmSend"), NamedTextColor.GREEN,
            "/gg-split confirm " + token))
        .append(Component.text(" "))
        .append(this.action(this.translate("splitConfirmCancel"), NamedTextColor.RED,
            "/gg-split cancel " + token));
    this.griefergames.displayAddonMessage(message);
  }

  private Component action(String label, TextColor color, String command) {
    return Component.text("[" + label + "]", Style.builder()
        .color(color)
        .clickEvent(ClickEvent.runCommand(command))
        .hoverEvent(HoverEvent.showText(Component.text(label)))
        .build());
  }

  private void showSending(int number, int total) {
    this.showRaw(this.translate("splitSending")
        .replace("{n}", Integer.toString(number))
        .replace("{total}", Integer.toString(total)));
  }

  private void show(String key) {
    this.showRaw(this.translate(key));
  }

  private void showRaw(String text) {
    this.griefergames.displayAddonMessage(Component.text(text, NamedTextColor.GRAY));
  }

  private String translate(String key) {
    return I18n.translate(this.griefergames.namespace() + ".messages." + key);
  }

  static int clampParts(int configured) {
    return Math.min(4, Math.max(2, configured));
  }

  static long delayMs(float seconds) {
    long delay = Math.round(seconds * 1000.0f);
    return Math.max(MINIMUM_DELAY_MS, delay);
  }

  private record PendingConfirm(int token, List<String> parts, long delayMs) {
  }
}
