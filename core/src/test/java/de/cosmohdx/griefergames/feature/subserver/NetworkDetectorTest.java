package de.cosmohdx.griefergames.feature.subserver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.cosmohdx.griefergames.core.CloudRegionType;
import de.cosmohdx.griefergames.feature.subserver.NetworkDetector.CloudServer;
import java.util.List;
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
  void numberedServerValueTeamIsTheLegacyNetwork() {
    assertEquals(
        Optional.of("lobby"),
        NetworkDetector.legacySubServer("5_10_value", "Lobby", "Server"));
    assertEquals(
        Optional.of("nature"),
        NetworkDetector.legacySubServer("5_10_value", "Nature", "Server"));
    assertTrue(NetworkDetector.legacySubServer("5_10_value", "Lobby", "Server-Adresse").isEmpty());
    assertTrue(NetworkDetector.legacySubServer("4_19_value", "0$", "Kontostand").isEmpty());
  }

  @Test
  void tabHeaderAktuellerServerIsTheLegacyNetwork() {
    assertEquals(
        Optional.of("lobby"),
        NetworkDetector.legacyFromTabHeader(
            "GrieferGames.net – Server-Netzwerk\nAktueller Server: Lobby"));
    assertEquals(
        Optional.of("nature"),
        NetworkDetector.legacyFromTabHeader("Aktueller Server: Nature"));
    assertTrue(NetworkDetector.legacyFromTabHeader("Willkommen auf cb1-1").isEmpty());
    assertTrue(NetworkDetector.legacyFromTabHeader("hello").isEmpty());
  }

  @Test
  void entryOrSuffixAloneIsEnoughForTheLegacyNetwork() {
    assertEquals(
        Optional.of("nature"),
        NetworkDetector.legacySubServer("server_value", List.of("", "Nature", "")));
    assertEquals(
        Optional.of("nature"),
        NetworkDetector.legacySubServer("server_value", List.of("", "", "§", "Nature")));
    assertEquals(
        Optional.of("cb12"),
        NetworkDetector.legacySubServer("server_value", List.of("", "CB12", "")));
    assertEquals(
        Optional.of("nature"),
        NetworkDetector.legacySubServer("server_value", List.of("", "§aNature")));
  }

  @Test
  void loadingOrBlankScoreboardDoesNotDetectANetwork() {
    assertTrue(NetworkDetector.legacySubServer("server_value", "Lade...").isEmpty());
    assertTrue(NetworkDetector.legacySubServer("server_value", "Server wird geladen").isEmpty());
    assertTrue(NetworkDetector.legacySubServer("server_value", "   ").isEmpty());
    assertTrue(NetworkDetector.legacySubServer("server_value", "").isEmpty());
    assertTrue(NetworkDetector.legacySubServer("server_value", (String) null).isEmpty());
  }

  @Test
  void otherScoreboardTeamsAreNotTheLegacyNetwork() {
    assertTrue(NetworkDetector.legacySubServer(null, "nature").isEmpty());
    assertTrue(NetworkDetector.legacySubServer("Server_Value", "nature").isEmpty());
    assertTrue(NetworkDetector.legacySubServer("money_value", "Bedwars").isEmpty());
    assertTrue(NetworkDetector.legacySubServer("server_value ", "nature").isEmpty());
  }

  @Test
  void sidebarLinesMatchKnownLegacyServers() {
    assertEquals(Optional.of("nature"), NetworkDetector.legacySidebarLine("Nature"));
    assertEquals(Optional.of("cb12"), NetworkDetector.legacySidebarLine("CB12"));
    assertEquals(Optional.of("extreme"), NetworkDetector.legacySidebarLine(" extreme "));
    assertTrue(NetworkDetector.legacySidebarLine("12.345.67$").isEmpty());
    assertTrue(NetworkDetector.legacySidebarLine("Online").isEmpty());
    assertTrue(NetworkDetector.legacySidebarLine("Lade...").isEmpty());
  }

  @Test
  void grieferGamesHostsIncludeCommonJoinAddresses() {
    assertTrue(NetworkDetector.isGrieferGamesHost("mc.griefergames.net"));
    assertTrue(NetworkDetector.isGrieferGamesHost("play.griefergames.net"));
    assertTrue(NetworkDetector.isGrieferGamesHost("cloud.griefergames.net"));
    assertTrue(NetworkDetector.isGrieferGamesHost("griefergames.net"));
    assertTrue(NetworkDetector.isGrieferGamesHost("GrieferGames.de"));
    assertTrue(NetworkDetector.isGrieferGamesHost("join.griefergames.live"));
    assertTrue(NetworkDetector.isGrieferGamesHost("something.griefergames.cloud"));
    assertTrue(NetworkDetector.isGrieferGamesHost("griefergames"));
    assertFalse(NetworkDetector.isGrieferGamesHost("griefergames.net.example.com"));
    assertFalse(NetworkDetector.isGrieferGamesHost("notgriefergames.net"));
    assertFalse(NetworkDetector.isGrieferGamesHost("My GrieferGames Server"));
    assertFalse(NetworkDetector.isGrieferGamesHost("hypixel.net"));
    assertFalse(NetworkDetector.isGrieferGamesHost("192.168.1.1"));
    assertFalse(NetworkDetector.isGrieferGamesHost(""));
    assertFalse(NetworkDetector.isGrieferGamesHost(null));
  }

  @Test
  void pluginChannelsAreRecognizedButDoNotDefinePresence() {
    assertTrue(NetworkDetector.isGrieferGamesPluginChannel("griefergames"));
    assertTrue(NetworkDetector.isGrieferGamesPluginChannel("MysteryMod"));
    assertFalse(NetworkDetector.isGrieferGamesPluginChannel("minecraft"));
    assertFalse(NetworkDetector.isGrieferGamesPluginChannel(null));
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
