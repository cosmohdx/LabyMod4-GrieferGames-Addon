package de.cosmohdx.griefergames.feature.server;

import de.cosmohdx.griefergames.GrieferGames;
import de.cosmohdx.griefergames.core.SubServerType;
import de.cosmohdx.griefergames.feature.subserver.NetworkTypeUpdater;
import de.cosmohdx.griefergames.payload.PayloadDebug;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.network.server.ServerDisconnectEvent;

public class GGServerQuitListener {
  private final GrieferGames griefergames;

  public GGServerQuitListener(GrieferGames griefergames) {
    this.griefergames = griefergames;
  }

  @Subscribe
  public void onServerQuit(ServerDisconnectEvent event) {
    NetworkTypeUpdater.apply(griefergames, SubServerType.UNKNOWN);
    if(griefergames.state().isOnGrieferGames()) {
      PayloadDebug.log(griefergames, "disconnect, incoming payloads are dropped until the next join");
      griefergames.state().setOnGrieferGames(false);
      griefergames.state().setSecondChat(null);
    }
  }
}
