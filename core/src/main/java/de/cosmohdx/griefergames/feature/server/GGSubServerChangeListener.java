package de.cosmohdx.griefergames.feature.server;

import de.cosmohdx.griefergames.GrieferGames;
import net.labymod.api.Laby;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.NamedTextColor;
import net.labymod.api.client.network.server.ServerData;
import net.labymod.api.event.Subscribe;
import net.labymod.api.labyconnect.LabyConnectSession;
import net.labymod.api.thirdparty.discord.DiscordActivity;
import net.labymod.api.thirdparty.discord.DiscordApp;
import net.labymod.api.util.I18n;
import java.util.concurrent.TimeUnit;

public class GGSubServerChangeListener {
  private final GrieferGames griefergames;

  public GGSubServerChangeListener(GrieferGames griefergames) {
    this.griefergames = griefergames;
  }

  @Subscribe
  public void onSubServerChange(GGSubServerChangeEvent event) {
    String formattedServerName = griefergames.helper().formatServerName(event.subServerName());

    griefergames.boosterController().resetBoosters();

    if (griefergames.configuration().friends().isEnabled()
        && griefergames.configuration().friends().discordShowSubServerEnabled().get()) {
      DiscordApp discordApp = Laby.references().discordApp();
      DiscordActivity previousActivity = discordApp.getDisplayedActivity();
      if (previousActivity != null) {
        DiscordActivity activity = DiscordActivity.builder(griefergames, previousActivity)
            .state("GrieferGames " + formattedServerName)
            .start()
            .build();
        discordApp.displayActivity(activity);
      }
    }

    if (griefergames.configuration().friends().isEnabled()
        && griefergames.configuration().friends().labyChatShowSubServerEnabled().get()
        && Laby.references().labyConnect().isAuthenticated()) {
      LabyConnectSession session = Laby.references().labyConnect().getSession();
      ServerData serverData = Laby.labyAPI().serverController().getCurrentServerData();
      if (session != null && serverData != null) {
        session.sendCurrentServer(serverData, "GrieferGames " + formattedServerName, false);
      }
    }

    if (griefergames.helper().isCityBuild(event.subServerName())) {
      if (!griefergames.isCitybuildDelay()) {
        griefergames.setWaitTime(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(15));
      }
      griefergames.setCitybuildDelay(false);
      if (griefergames.configuration().automations().isSendSubServerEnabled()) {
        griefergames.displayAddonMessage(Component.text(
            I18n.translate(griefergames.namespace() + ".messages.citybuildJoin")
                .replace("{citybuild}", formattedServerName),
            NamedTextColor.GRAY
        ));
      }
    } else if (event.subServerName().equals("portal")) {
      if (!griefergames.isCitybuildDelay()) {
        griefergames.setWaitTime(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(12));
      }
    } else if (event.subServerName().equals("skyblock")) {
      if (!griefergames.isCitybuildDelay()) {
        griefergames.setWaitTime(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(15));
      }
    } else if (event.subServerName().equals("lobby")) {
      if (griefergames.configuration().automations().isAutoPortalEnabled()) {
        griefergames.schedule(() -> griefergames.sendMessage("/portal"), 500, TimeUnit.MILLISECONDS);
      }
    }
  }
}
