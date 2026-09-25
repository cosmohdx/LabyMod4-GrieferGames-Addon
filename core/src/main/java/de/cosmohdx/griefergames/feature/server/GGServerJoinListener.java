package de.cosmohdx.griefergames.feature.server;

import de.cosmohdx.griefergames.GrieferGames;
import de.cosmohdx.griefergames.core.SubServerType;
import de.cosmohdx.griefergames.feature.subserver.NetworkTypeUpdater;
import de.cosmohdx.griefergames.payload.PayloadDebug;
import net.labymod.api.Laby;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.NamedTextColor;
import net.labymod.api.client.component.format.Style;
import net.labymod.api.client.component.format.TextDecoration;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.resources.ResourceLocation;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.network.server.ServerJoinEvent;
import net.labymod.api.notification.Notification;
import net.labymod.api.util.I18n;

public class GGServerJoinListener {
  private final GrieferGames griefergames;

  public GGServerJoinListener(GrieferGames griefergames) {
    this.griefergames = griefergames;
  }

  @Subscribe
  public void onServerJoin(ServerJoinEvent event) {
    NetworkTypeUpdater.apply(griefergames, SubServerType.UNKNOWN);
    String host = event.serverData().address().getHost().toLowerCase();
    boolean onGrieferGames = host.endsWith("griefergames.net")
        || host.endsWith("griefergames.de")
        || host.endsWith("griefergames.live");
    if (onGrieferGames) {
      griefergames.state().setOnGrieferGames(true);
      griefergames.state().setLastActivity(System.currentTimeMillis());
      griefergames.state().setAfk(false);
      griefergames.state().setWaitTime(0);
      griefergames.state().setCitybuildDelay(false);
      griefergames.state().setNickname(null);
      griefergames.state().setHideBoosterMenu(false);
      griefergames.helper().findSecondChat();

      // Warn user when advanced chat is disabled.
      if(!Laby.labyAPI().config().ingame().advancedChat().enabled().get()) {
        griefergames.displayAddonMessage(Component.text(I18n.translate(griefergames.namespace()+".messages.advancedChatWarning"),
            Style.builder().color(NamedTextColor.RED).decorate(TextDecoration.BOLD).build()));
        Laby.labyAPI().notificationController().push(Notification.builder()
            .title(Component.text("GrieferGames-Addon", NamedTextColor.GOLD))
            .text(Component.text(I18n.translate(griefergames.namespace()+".notifications.generalError"), NamedTextColor.RED))
            .icon(Icon.texture(ResourceLocation.create(griefergames.namespace(), "textures/error.png"))).build());
      }
    }
    PayloadDebug.log(griefergames, onGrieferGames
        ? "joined " + host + ", incoming payloads are accepted"
        : "joined " + host + ", incoming payloads stay dropped");
  }
}
