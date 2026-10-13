package de.cosmohdx.griefergames.feature.subserver;

import de.cosmohdx.griefergames.core.CloudRegionType;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jetbrains.annotations.Nullable;

/**
 * Reads the GrieferGames network from scoreboard and tab-list text.
 * Callers pass plain strings, so the rules do not depend on the client.
 */
public final class NetworkDetector {

  private static final Pattern LEGACY_CB = Pattern.compile("^cb[0-9]+$");
  private static final Pattern LEGACY_CITYBUILD = Pattern.compile("^citybuild.*");
  private static final Pattern LEGACY_FARM = Pattern.compile("^farm(world|welt).*");
  private static final Pattern TAB_CURRENT_SERVER =
      Pattern.compile("(?i)aktueller\\s+server:\\s*(\\S+)");
  private static final Pattern VALUE_TEAM = Pattern.compile("^(.*)_value$");
  private static final Pattern TITLE_TEAM = Pattern.compile("^(.*)_title$");

  private NetworkDetector() {
  }

  /**
   * 1.8 scoreboard team {@code server_value}, or the newer {@code <id>_value} row whose paired
   * title is the Server line. Empty while the name is blank or still loading.
   */
  public static Optional<String> legacySubServer(
      String teamName,
      @Nullable String displayPlainText,
      @Nullable String pairedTitlePlainText) {
    if (!isLegacyServerValueTeam(teamName, pairedTitlePlainText)) {
      return Optional.empty();
    }
    return normalizeLegacyName(displayPlainText);
  }

  /**
   * @deprecated use {@link #legacySubServer(String, String, String)}
   */
  @Deprecated
  public static Optional<String> legacySubServer(String teamName, @Nullable String displayPlainText) {
    return legacySubServer(teamName, displayPlainText, null);
  }

  /**
   * First usable display candidate for a legacy server-value team.
   */
  public static Optional<String> legacySubServer(
      String teamName,
      Iterable<String> candidates,
      @Nullable String pairedTitlePlainText) {
    if (!isLegacyServerValueTeam(teamName, pairedTitlePlainText) || candidates == null) {
      return Optional.empty();
    }
    for (String candidate : candidates) {
      Optional<String> name = normalizeLegacyName(candidate);
      if (name.isPresent()) {
        return name;
      }
    }
    return Optional.empty();
  }

  public static Optional<String> legacySubServer(String teamName, Iterable<String> candidates) {
    return legacySubServer(teamName, candidates, null);
  }

  /**
   * Newer scoreboards use {@code 5_10_title} = „Server“ and {@code 5_10_value} = „Nature“.
   * Classic boards still use the exact team name {@code server_value}.
   */
  public static boolean isLegacyServerValueTeam(
      @Nullable String teamName,
      @Nullable String pairedTitlePlainText) {
    if ("server_value".equals(teamName)) {
      return true;
    }
    if (teamName == null || !VALUE_TEAM.matcher(teamName).matches()) {
      return false;
    }
    return isServerRowLabel(pairedTitlePlainText);
  }

  /**
   * Title text of the scoreboard row that shows the current sub-server
   * ({@code Server} / {@code ᐅ Server}), not the address row.
   */
  public static boolean isServerRowLabel(@Nullable String titlePlainText) {
    if (titlePlainText == null || titlePlainText.isBlank()) {
      return false;
    }
    String text = titlePlainText.toLowerCase(Locale.ROOT).replaceAll("§.", "").replace("§", "");
    if (text.contains("adresse") || text.contains("address")) {
      return false;
    }
    return text.contains("server");
  }

  public static Optional<String> titleBase(@Nullable String teamName) {
    if (teamName == null) {
      return Optional.empty();
    }
    Matcher matcher = TITLE_TEAM.matcher(teamName);
    if (!matcher.matches()) {
      return Optional.empty();
    }
    return Optional.of(matcher.group(1));
  }

  public static Optional<String> valueBase(@Nullable String teamName) {
    if (teamName == null) {
      return Optional.empty();
    }
    Matcher matcher = VALUE_TEAM.matcher(teamName);
    if (!matcher.matches()) {
      return Optional.empty();
    }
    return Optional.of(matcher.group(1));
  }

  /**
   * Tab-list header line {@code Aktueller Server: Nature} on the 1.8 network.
   */
  public static Optional<String> legacyFromTabHeader(@Nullable String headerPlainText) {
    if (headerPlainText == null || headerPlainText.isBlank()) {
      return Optional.empty();
    }
    // Cloud headers use cb1-/farm- tokens; those are handled separately.
    if (cloudServer(headerPlainText).isPresent()) {
      return Optional.empty();
    }
    Matcher matcher = TAB_CURRENT_SERVER.matcher(headerPlainText);
    if (!matcher.find()) {
      return Optional.empty();
    }
    return normalizeLegacyName(matcher.group(1));
  }

  /**
   * Sidebar line ({@code prefix + entry + suffix}) that names a known 1.8 sub-server.
   */
  public static Optional<String> legacySidebarLine(@Nullable String linePlainText) {
    Optional<String> name = normalizeLegacyName(linePlainText);
    if (name.isEmpty() || !isKnownLegacySubServer(name.get())) {
      return Optional.empty();
    }
    return name;
  }

  /**
   * Lower-case name when the text is a finished 1.8 sub-server label (not loading / blank).
   */
  public static Optional<String> normalizeLegacyName(@Nullable String plainText) {
    if (plainText == null) {
      return Optional.empty();
    }
    String subServerName = plainText
        .toLowerCase(Locale.ROOT)
        .replaceAll("§.", "")
        .replace("§", "")
        .replaceAll("[^a-z0-9_\\-]", "")
        .trim();
    if (subServerName.isBlank() || subServerName.contains("lade")) {
      return Optional.empty();
    }
    return Optional.of(subServerName);
  }

  public static boolean isKnownLegacySubServer(String normalizedName) {
    if (normalizedName == null || normalizedName.isBlank()) {
      return false;
    }
    return normalizedName.equals("nature")
        || normalizedName.equals("extreme")
        || normalizedName.equals("evil")
        || normalizedName.equals("cbe")
        || normalizedName.equals("lava")
        || normalizedName.equals("wasser")
        || normalizedName.equals("event")
        || normalizedName.equals("lobby")
        || normalizedName.equals("portal")
        || LEGACY_CB.matcher(normalizedName).matches()
        || LEGACY_CITYBUILD.matcher(normalizedName).matches()
        || LEGACY_FARM.matcher(normalizedName).matches();
  }

  /**
   * Tab-list header that names a cloud region
   * ({@code cb1-}, {@code farm-}, {@code jail-}, {@code minigame-}, {@code event-}).
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

  /**
   * True when the connected host belongs to GrieferGames (exact domain or subdomain).
   * Does not match hosts that merely contain the domain as a prefix of another label
   * (e.g. {@code griefergames.net.example.com}).
   */
  public static boolean isGrieferGamesHost(@Nullable String host) {
    if (host == null || host.isBlank()) {
      return false;
    }
    String value = host.toLowerCase(Locale.ROOT).trim();
    if (value.startsWith("[") && value.contains("]")) {
      return false;
    }
    return matchesDomain(value, "griefergames.net")
        || matchesDomain(value, "griefergames.de")
        || matchesDomain(value, "griefergames.live")
        || matchesDomain(value, "griefergames.cloud")
        || matchesDomain(value, "griefergames.com")
        || matchesDomain(value, "griefergam.es")
        || value.equals("griefergames");
  }

  private static boolean matchesDomain(String host, String domain) {
    return host.equals(domain) || host.endsWith("." + domain);
  }

  public static boolean isGrieferGamesPluginChannel(@Nullable String namespace) {
    if (namespace == null || namespace.isBlank()) {
      return false;
    }
    String value = namespace.toLowerCase(Locale.ROOT);
    return value.equals("griefergames") || value.equals("mysterymod");
  }

  public record CloudServer(CloudRegionType regionType, String serverName) {
  }
}
