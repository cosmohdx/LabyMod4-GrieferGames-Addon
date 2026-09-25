package de.cosmohdx.griefergames.feature.nearby;

import de.cosmohdx.griefergames.GrieferGames;
import de.cosmohdx.griefergames.core.CloudRegionType;
import de.cosmohdx.griefergames.core.SubServerType;
import de.cosmohdx.griefergames.feature.subserver.GGNetworkTypeChangeEvent;
import de.cosmohdx.griefergames.feature.subserver.GGSubServerChangeEvent;
import de.cosmohdx.griefergames.feature.subserver.NetworkDetector;
import net.labymod.api.Laby;
import net.labymod.api.client.scoreboard.TabList;
import net.labymod.api.event.Phase;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.lifecycle.GameTickEvent;
import org.jetbrains.annotations.Nullable;

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
    boolean active = this.active();
    if (!active) {
      this.service.tick(false, null, null);
      return;
    }
    NearbyPlayersConfig config = this.griefergames.configuration().nearbyPlayers();
    NearbyQuery query = new NearbyQuery(
        true,
        config.radius(),
        config.limit(),
        config.onlyLineOfSight(),
        config.onlyOnCitybuild(),
        this.griefergames.state().getSubServerType(),
        this.griefergames.state().getSubServer(),
        this.cloudRegion());
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

  private boolean active() {
    if (!this.griefergames.state().isOnGrieferGames() || !this.griefergames.configuration().enabled().get()) {
      return false;
    }
    NearbyPlayersConfig config = this.griefergames.configuration().nearbyPlayers();
    if (!config.isEnabled() || !this.griefergames.state().isNetworkKnown()) {
      return false;
    }
    return NearbyServerGate.allows(
        config.onlyOnCitybuild(),
        this.griefergames.state().getSubServerType(),
        this.griefergames.state().getSubServer(),
        this.cloudRegion());
  }

  @Nullable
  private CloudRegionType cloudRegion() {
    if (this.griefergames.state().getSubServerType() != SubServerType.CLOUD) {
      return null;
    }
    TabList tabList = Laby.labyAPI().minecraft().getTabList();
    if (tabList == null || tabList.header() == null) {
      return null;
    }
    String header = this.griefergames.helper().componentToPlainText(tabList.header());
    return NetworkDetector.cloudServer(header).map(NetworkDetector.CloudServer::regionType).orElse(null);
  }
}
