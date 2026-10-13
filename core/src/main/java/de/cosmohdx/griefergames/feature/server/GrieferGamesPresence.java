package de.cosmohdx.griefergames.feature.server;

import de.cosmohdx.griefergames.GrieferGames;
import de.cosmohdx.griefergames.feature.subserver.NetworkDetector;
import de.cosmohdx.griefergames.payload.PayloadDebug;
import net.labymod.api.Laby;
import net.labymod.api.client.network.server.ServerAddress;
import net.labymod.api.client.network.server.ServerData;
import org.jetbrains.annotations.Nullable;

/**
 * Marks the client as being on GrieferGames from the join / resolved host only.
 */
public final class GrieferGamesPresence {

  private GrieferGamesPresence() {
  }

  /**
   * @return true when this call flipped the flag from off to on
   */
  public static boolean enter(GrieferGames griefergames, String reason) {
    if (griefergames.state().isOnGrieferGames()) {
      return false;
    }
    griefergames.state().setOnGrieferGames(true);
    griefergames.state().setLastActivity(System.currentTimeMillis());
    griefergames.state().setAfk(false);
    griefergames.state().setWaitTime(0);
    griefergames.state().setCitybuildDelay(false);
    griefergames.state().setNickname(null);
    griefergames.state().setHideBoosterMenu(false);
    griefergames.helper().findSecondChat();
    PayloadDebug.log(griefergames, "on GrieferGames via " + reason);
    return true;
  }

  public static void leave(GrieferGames griefergames) {
    if (!griefergames.state().isOnGrieferGames()) {
      return;
    }
    PayloadDebug.log(griefergames, "left GrieferGames");
    griefergames.state().setOnGrieferGames(false);
    griefergames.state().setSecondChat(null);
  }

  /**
   * Clears the GrieferGames flag and related session state. Call on every join before re-detecting.
   */
  public static void reset(GrieferGames griefergames) {
    leave(griefergames);
  }

  /**
   * @return true when at least one host matched GrieferGames (flag is on afterwards)
   */
  public static boolean enterFromHosts(GrieferGames griefergames, String... hosts) {
    for (String host : hosts) {
      if (NetworkDetector.isGrieferGamesHost(host)) {
        enter(griefergames, "host " + host);
        return true;
      }
    }
    return false;
  }

  /**
   * @return true when the server address identifies GrieferGames
   */
  public static boolean enterFromServerData(GrieferGames griefergames, @Nullable ServerData serverData) {
    if (serverData == null) {
      return false;
    }
    return enterFromHosts(
        griefergames,
        hostOf(serverData.address()),
        hostOf(serverData.actualAddress()));
  }

  /**
   * Re-reads the current LabyMod server entry. Useful after joins where the event host was
   * empty/IP-only, or when the resolved address becomes available later.
   *
   * @return true when the current server identifies GrieferGames
   */
  public static boolean refreshFromCurrentServer(GrieferGames griefergames) {
    if (griefergames.state().isOnGrieferGames()) {
      return true;
    }
    if (!Laby.labyAPI().serverController().isConnected()) {
      return false;
    }
    return enterFromServerData(griefergames, Laby.labyAPI().serverController().getCurrentServerData());
  }

  public static String hostOf(@Nullable ServerAddress address) {
    return address == null || address.getHost() == null ? "" : address.getHost();
  }
}
