package de.cosmohdx.griefergames.feature.friends;

import de.cosmohdx.griefergames.GrieferGames;
import de.cosmohdx.griefergames.feature.subserver.GGSubServerChangeEvent;
import net.labymod.api.Laby;
import net.labymod.api.client.network.server.ServerData;
import net.labymod.api.event.Subscribe;
import net.labymod.api.labyconnect.LabyConnectSession;
import net.labymod.api.thirdparty.discord.DiscordActivity;
import net.labymod.api.thirdparty.discord.DiscordApp;

public class FriendsPresenceListener {

  private final GrieferGames griefergames;

  public FriendsPresenceListener(GrieferGames griefergames) {
    this.griefergames = griefergames;
  }

  @Subscribe
  public void onSubServerChange(GGSubServerChangeEvent event) {
    String formattedServerName = griefergames.helper().formatServerName(event.subServerName());

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
  }
}
