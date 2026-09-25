package de.cosmohdx.griefergames.feature.server;

import de.cosmohdx.griefergames.GrieferGames;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.network.server.ServerDisconnectEvent;

public class GGServerQuitListener {
  private final GrieferGames griefergames;

  public GGServerQuitListener(GrieferGames griefergames) {
    this.griefergames = griefergames;
  }

  @Subscribe
  public void onServerQuit(ServerDisconnectEvent event) {
    if(griefergames.isOnGrieferGames()) {
      griefergames.setOnGrieferGames(false);
      griefergames.setSecondChat(null);
    }
  }
}
