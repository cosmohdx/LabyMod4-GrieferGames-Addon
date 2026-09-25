package de.cosmohdx.griefergames.feature.remover;

import de.cosmohdx.griefergames.GrieferGames;
import de.cosmohdx.griefergames.feature.chat.ChatModule;
import de.cosmohdx.griefergames.feature.chat.GGChatProcessEvent;
import de.cosmohdx.griefergames.payload.model.ClearLagPayload;
import de.cosmohdx.griefergames.payload.model.EntityRemoverPayload;
import net.labymod.api.Laby;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.event.HoverEvent;
import net.labymod.api.client.component.format.NamedTextColor;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.resources.ResourceLocation;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.network.server.ServerDisconnectEvent;
import net.labymod.api.notification.Notification;
import net.labymod.api.util.I18n;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Remover extends ChatModule {

  private static final long NOTIFY_WITHIN_SECONDS = 60;

  private final GrieferGames griefergames;
  private final Pattern itemWarning = Pattern.compile(
      "^\\[GrieferGames\\] Warnung! Die auf dem Boden liegenden Items werden in ([0-9]+) Sekunden entfernt!$");
  private final Pattern itemDone = Pattern.compile(
      "^\\[GrieferGames\\] Es wurden ([0-9]+) auf dem Boden liegende Items entfernt!$");
  private final Pattern mobWarning = Pattern.compile(
      "^\\[MobRemover\\] Achtung! In ([0-9]+) Minuten? werden alle Tiere gelöscht\\.$");
  private final Pattern mobDone = Pattern.compile(
      "^\\[MobRemover\\] Es wurden ([0-9]+) Tiere entfernt\\.$");
  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
  private long previousItemSeconds = Long.MAX_VALUE;
  private long previousEntitySeconds = Long.MAX_VALUE;

  public Remover(GrieferGames griefergames) {
    this.griefergames = griefergames;
    griefergames.payloads().subscribe(ClearLagPayload.class, payload ->
        this.onCountdown(payload.known(), payload.remainingSeconds(), true));
    griefergames.payloads().subscribe(EntityRemoverPayload.class, payload ->
        this.onCountdown(payload.known(), payload.remainingSeconds(), false));
  }

  @Subscribe
  public void onServerQuit(ServerDisconnectEvent event) {
    this.previousItemSeconds = Long.MAX_VALUE;
    this.previousEntitySeconds = Long.MAX_VALUE;
  }

  private void onCountdown(boolean known, long remaining, boolean items) {
    if (!known) {
      this.remember(items, Long.MAX_VALUE);
      return;
    }
    long previous = items ? this.previousItemSeconds : this.previousEntitySeconds;
    this.remember(items, remaining);
    if (remaining > previous || previous <= NOTIFY_WITHIN_SECONDS || remaining > NOTIFY_WITHIN_SECONDS) {
      return;
    }
    if (!this.griefergames.configuration().remover().notification()) {
      return;
    }
    if (items) {
      this.push("ItemRemover", "textures/itemremover.png", "notifications.remover.items", Long.toString(remaining));
    } else {
      long minutes = Math.max(1, (remaining + 59) / 60);
      this.push("MobRemover", "textures/mobremover.png", "notifications.remover.mobs", Long.toString(minutes));
    }
  }

  private void remember(boolean items, long remaining) {
    if (items) {
      this.previousItemSeconds = remaining;
    } else {
      this.previousEntitySeconds = remaining;
    }
  }

  @Subscribe
  public void messageProcessEvent(GGChatProcessEvent event) {
    if (event.isCancelled()) {
      return;
    }
    String plain = event.getMessage().getPlainText();
    if (plain.isBlank()) {
      return;
    }

    Matcher itemWarningMatch = this.itemWarning.matcher(plain);
    Matcher itemDoneMatch = this.itemDone.matcher(plain);
    Matcher mobWarningMatch = this.mobWarning.matcher(plain);
    Matcher mobDoneMatch = this.mobDone.matcher(plain);

    boolean itemsCleared = itemDoneMatch.find();
    boolean itemWarningFound = !itemsCleared && itemWarningMatch.find();
    boolean mobsCleared = mobDoneMatch.find();
    boolean mobWarningFound = !mobsCleared && mobWarningMatch.find();
    if (!itemsCleared && !itemWarningFound && !mobsCleared && !mobWarningFound) {
      return;
    }

    RemoverConfig config = this.griefergames.configuration().remover();
    if (config.lastTimeHover() && (itemsCleared || mobsCleared)) {
      Component hoverText = Component.text(LocalDateTime.now().format(this.formatter));
      event.getMessage().component().style(
          event.getMessage().component().style().hoverEvent(HoverEvent.showText(hoverText)));
    }

    if (this.griefergames.configuration().chat().routeRemover()) {
      event.setSecondChat(true);
    }

    if (!config.notification() || itemsCleared || mobsCleared) {
      return;
    }
    if (itemWarningFound) {
      this.push("ItemRemover", "textures/itemremover.png", "notifications.remover.items", itemWarningMatch.group(1));
    } else {
      this.push("MobRemover", "textures/mobremover.png", "notifications.remover.mobs", mobWarningMatch.group(1));
    }
  }

  private void push(String title, String icon, String textKey, String time) {
    Laby.labyAPI().notificationController().push(Notification.builder()
        .title(Component.text(title, NamedTextColor.RED))
        .text(Component.text(I18n.translate(this.griefergames.namespace() + "." + textKey).replace("{time}", time)))
        .icon(Icon.texture(ResourceLocation.create(this.griefergames.namespace(), icon)))
        .build());
  }
}
