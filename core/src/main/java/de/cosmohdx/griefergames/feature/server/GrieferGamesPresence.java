package de.cosmohdx.griefergames.feature.server;

import de.cosmohdx.griefergames.GrieferGames;
import de.cosmohdx.griefergames.feature.subserver.NetworkDetector;
import de.cosmohdx.griefergames.payload.PayloadDebug;
import java.util.Locale;
import net.labymod.api.Laby;
import net.labymod.api.client.network.server.ServerAddress;
import net.labymod.api.client.network.server.ServerData;
import org.jetbrains.annotations.Nullable;

/**
 * Marks the client as being on GrieferGames from several independent signals:
 * join address, current server data, scoreboard layout, cloud tab header and
 * GrieferGames / MysteryMod plugin channels.
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
   * @return true when the server data identifies GrieferGames
   */
  public static boolean enterFromServerData(GrieferGames griefergames, @Nullable ServerData serverData) {
    if (serverData == null) {
      return false;
    }
    if (enterFromHosts(
        griefergames,
        hostOf(serverData.address()),
        hostOf(serverData.actualAddress()),
        serverData.getName())) {
      return true;
    }
    String name = serverData.getName();
    if (name != null && name.toLowerCase(Locale.ROOT).contains("griefergames")) {
      enter(griefergames, "server name " + name);
      return true;
    }
    return false;
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

  /**
   * @return true when the scoreboard layout identifies GrieferGames
   */
  public static boolean enterFromScoreboardTeams(GrieferGames griefergames, Iterable<String> teamNames) {
    if (!NetworkDetector.looksLikeGrieferGamesScoreboard(teamNames)) {
      return false;
    }
    enter(griefergames, "scoreboard");
    return true;
  }

  /**
   * @return true when the plugin-channel namespace identifies GrieferGames
   */
  public static boolean enterFromPluginChannel(GrieferGames griefergames, @Nullable String namespace) {
    if (!NetworkDetector.isGrieferGamesPluginChannel(namespace)) {
      return false;
    }
    enter(griefergames, "plugin channel " + namespace);
    return true;
  }

  /**
   * @return true when the tab header identifies the cloud network
   */
  public static boolean enterFromCloudHeader(GrieferGames griefergames, @Nullable String headerPlainText) {
    if (NetworkDetector.cloudServer(headerPlainText).isEmpty()) {
      return false;
    }
    enter(griefergames, "cloud tab header");
    return true;
  }

  public static String hostOf(@Nullable ServerAddress address) {
    return address == null || address.getHost() == null ? "" : address.getHost();
  }
}
