package de.cosmohdx.griefergames.feature.nearby;

import de.cosmohdx.griefergames.GrieferGames;
import de.cosmohdx.griefergames.feature.subserver.GGNetworkTypeChangeEvent;
import de.cosmohdx.griefergames.feature.subserver.GGSubServerChangeEvent;
import net.labymod.api.event.Phase;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.lifecycle.GameTickEvent;

public class NearbyPlayersListener {

  private final GrieferGames griefergames;
  private final NearbyPlayersService service;
  private final NearbyPlayerCollector collector;

  public NearbyPlayersListener(GrieferGames griefergames, NearbyPlayersService service) {
    this.griefergames = griefergames;
    this.service = service;
    this.collector = new NearbyPlayerCollector(griefergames);
  }

  @Subscribe
  public void onTick(GameTickEvent event) {
    if (event.phase() != Phase.POST) {
      return;
    }
    if (!this.griefergames.state().isOnGrieferGames()
        || !this.griefergames.configuration().enabled().get()) {
      this.service.tick(false, null, null);
      return;
    }
    NearbyPlayersConfig config = this.griefergames.configuration().nearbyPlayers();
    if (!config.isEnabled()) {
      this.service.tick(false, null, null);
      return;
    }
    NearbyQuery query = new NearbyQuery(
        true,
        config.radius(),
        config.limit(),
        config.onlyLineOfSight(),
        false,
        this.griefergames.state().getSubServerType(),
        this.griefergames.state().getSubServer(),
        null);
    double radius = query.radius();
    this.service.tick(true, query, () -> this.collector.collect(radius));
  }

  @Subscribe
  public void onNetworkTypeChange(GGNetworkTypeChangeEvent event) {
    this.service.clear();
  }

  @Subscribe
  public void onSubServerChange(GGSubServerChangeEvent event) {
    this.service.clear();
  }
}
