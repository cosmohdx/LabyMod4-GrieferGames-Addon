package de.cosmohdx.griefergames.feature.subserver;

import de.cosmohdx.griefergames.core.CloudRegionType;
import java.util.Locale;
import java.util.Optional;

/**
 * Reads the GrieferGames network from scoreboard and tab-list text.
 * Callers pass plain strings, so the rules do not depend on the client.
 */
public final class NetworkDetector {

  private NetworkDetector() {
  }

  /**
   * 1.8 scoreboard team {@code server_value}. Empty while the name is blank or still loading.
   *
   * @param teamName scoreboard team name
   * @param prefixPlainText plain prefix, already without formatting
   * @return lower-case sub-server name when this is the 1.8 network
   */
  public static Optional<String> legacySubServer(String teamName, String prefixPlainText) {
    if (!"server_value".equals(teamName) || prefixPlainText == null) {
      return Optional.empty();
    }
    String subServerName = prefixPlainText.toLowerCase(Locale.ROOT);
    if (subServerName.isBlank() || subServerName.contains("lade")) {
      return Optional.empty();
    }
    return Optional.of(subServerName);
  }

  /**
   * Tab-list header that names a cloud region
   * ({@code cb1-}, {@code farm-}, {@code jail-}, {@code minigame-}, {@code event-}).
   *
   * @param headerPlainText plain tab-list header
   * @return the region when this is the cloud network
   */
  public static Optional<CloudServer> cloudServer(String headerPlainText) {
    String serverName = CloudRegionType.extractServerName(headerPlainText);
    if (serverName == null) {
      return Optional.empty();
    }
    CloudRegionType regionType = CloudRegionType.getRegionType(serverName);
    if (regionType == null) {
      return Optional.empty();
    }
    return Optional.of(new CloudServer(regionType, serverName));
  }

  public record CloudServer(CloudRegionType regionType, String serverName) {
  }
}
