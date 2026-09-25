package de.cosmohdx.griefergames.feature.redstone;

import de.cosmohdx.griefergames.GrieferGames;
import de.cosmohdx.griefergames.payload.model.RedstoneStatusPayload;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.network.server.ServerDisconnectEvent;

public class RedstoneListener {

  private final GrieferGames griefergames;

  public RedstoneListener(GrieferGames griefergames) {
    this.griefergames = griefergames;
    griefergames.payloads().subscribe(RedstoneStatusPayload.class, payload ->
        griefergames.state().setRedstoneActive(payload.active()));
  }

  @Subscribe
  public void onServerQuit(ServerDisconnectEvent event) {
    griefergames.state().setRedstoneActive(false);
  }
}
