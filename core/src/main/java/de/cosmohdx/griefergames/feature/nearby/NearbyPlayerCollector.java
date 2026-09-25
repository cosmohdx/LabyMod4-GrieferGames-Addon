package de.cosmohdx.griefergames.feature.nearby;

import de.cosmohdx.griefergames.GrieferGames;
import de.cosmohdx.griefergames.core.LoadedPlayerView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.labymod.api.Laby;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.entity.player.GameMode;
import net.labymod.api.client.network.ClientPacketListener;
import net.labymod.api.client.network.NetworkPlayerInfo;

/**
 * Reads client-loaded players and keeps only the tab-list display name.
 * Profile names are not resolved to a real name.
 */
public final class NearbyPlayerCollector {

  private final GrieferGames griefergames;

  public NearbyPlayerCollector(GrieferGames griefergames) {
    this.griefergames = griefergames;
  }

  public List<NearbyCandidate> collect(double radius) {
    List<LoadedPlayerView> loaded = this.griefergames.controller().loadedPlayersWithin(radius);
    if (loaded == null || loaded.isEmpty()) {
      return List.of();
    }
    Map<UUID, NetworkPlayerInfo> listed = tabList();
    List<NearbyCandidate> candidates = new ArrayList<>(loaded.size());
    for (LoadedPlayerView player : loaded) {
      if (player == null || player.uniqueId() == null) {
        continue;
      }
      NetworkPlayerInfo info = listed.get(player.uniqueId());
      Component displayName = info == null ? null : info.displayName();
      String name = player.name() == null ? "" : player.name();
      boolean spectator = player.spectator() || (info != null && info.gameMode() == GameMode.SPECTATOR);
      candidates.add(new NearbyCandidate(
          player.uniqueId(),
          name,
          displayName,
          player.distance(),
          false,
          player.invisible(),
          spectator,
          info != null && info.isListed(),
          player.lineOfSight()));
    }
    return candidates;
  }

  private static Map<UUID, NetworkPlayerInfo> tabList() {
    ClientPacketListener listener = Laby.labyAPI().minecraft().getClientPacketListener();
    if (listener == null) {
      return Map.of();
    }
    Map<UUID, NetworkPlayerInfo> byId = new HashMap<>();
    for (NetworkPlayerInfo info : listener.getNetworkPlayerInfos()) {
      if (info == null || info.profile() == null || info.profile().getUniqueId() == null) {
        continue;
      }
      byId.put(info.profile().getUniqueId(), info);
    }
    return byId;
  }
}
