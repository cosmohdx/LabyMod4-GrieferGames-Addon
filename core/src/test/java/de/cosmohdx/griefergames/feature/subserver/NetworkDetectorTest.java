package de.cosmohdx.griefergames.feature.subserver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.cosmohdx.griefergames.core.CloudRegionType;
import de.cosmohdx.griefergames.feature.subserver.NetworkDetector.CloudServer;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class NetworkDetectorTest {

  @Test
  void scoreboardTeamServerValueIsTheLegacyNetwork() {
    assertEquals(Optional.of("nature"), NetworkDetector.legacySubServer("server_value", "Nature"));
    assertEquals(Optional.of("cb12"), NetworkDetector.legacySubServer("server_value", "CB12"));
    assertEquals(Optional.of("extreme"), NetworkDetector.legacySubServer("server_value", "extreme"));
  }

  @Test
  void loadingOrBlankScoreboardDoesNotDetectANetwork() {
    assertTrue(NetworkDetector.legacySubServer("server_value", "Lade...").isEmpty());
    assertTrue(NetworkDetector.legacySubServer("server_value", "Server wird geladen").isEmpty());
    assertTrue(NetworkDetector.legacySubServer("server_value", "   ").isEmpty());
    assertTrue(NetworkDetector.legacySubServer("server_value", "").isEmpty());
    assertTrue(NetworkDetector.legacySubServer("server_value", null).isEmpty());
  }

  @Test
  void otherScoreboardTeamsAreNotTheLegacyNetwork() {
    assertTrue(NetworkDetector.legacySubServer(null, "nature").isEmpty());
    assertTrue(NetworkDetector.legacySubServer("Server_Value", "nature").isEmpty());
    assertTrue(NetworkDetector.legacySubServer("money_value", "Bedwars").isEmpty());
    assertTrue(NetworkDetector.legacySubServer("server_value ", "nature").isEmpty());
  }

  @Test
  void tabHeaderPrefixesAreTheCloudNetwork() {
    assertCloud("cb1-12", CloudRegionType.CITYBUILD, "cb1-12");
    assertCloud("Willkommen auf cb1-1", CloudRegionType.CITYBUILD, "cb1-1");
    assertCloud("farm-1", CloudRegionType.FARM, "farm-1");
    assertCloud("jail-a", CloudRegionType.JAIL, "jail-a");
    assertCloud("minigame-bedwars", CloudRegionType.MINIGAME, "minigame-bedwars");
    assertCloud("event-ostern", CloudRegionType.EVENT, "event-ostern");
  }

  @Test
  void headersWithoutARegionPrefixDoNotDetectTheCloud() {
    assertTrue(NetworkDetector.cloudServer(null).isEmpty());
    assertTrue(NetworkDetector.cloudServer("").isEmpty());
    assertTrue(NetworkDetector.cloudServer("nature").isEmpty());
    assertTrue(NetworkDetector.cloudServer("CityBuild").isEmpty());
    assertTrue(NetworkDetector.cloudServer("xcb1-1").isEmpty());
  }

  private static void assertCloud(String header, CloudRegionType regionType, String serverName) {
    Optional<CloudServer> detected = NetworkDetector.cloudServer(header);
    assertTrue(detected.isPresent());
    assertEquals(regionType, detected.get().regionType());
    assertEquals(serverName, detected.get().serverName());
  }
}
