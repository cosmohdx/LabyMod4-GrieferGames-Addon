package de.cosmohdx.griefergames.feature.subserver;

import de.cosmohdx.griefergames.GrieferGames;
import de.cosmohdx.griefergames.core.CloudRegionType;
import de.cosmohdx.griefergames.core.SubServerType;
import de.cosmohdx.griefergames.feature.server.GrieferGamesPresence;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.labymod.api.Laby;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.TextComponent;
import net.labymod.api.client.scoreboard.DisplaySlot;
import net.labymod.api.client.scoreboard.Scoreboard;
import net.labymod.api.client.scoreboard.ScoreboardObjective;
import net.labymod.api.client.scoreboard.ScoreboardScore;
import net.labymod.api.client.scoreboard.ScoreboardTeam;
import net.labymod.api.client.scoreboard.TabList;
import net.labymod.api.event.Phase;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.gui.screen.playerlist.PlayerListUpdateEvent;
import net.labymod.api.event.client.lifecycle.GameTickEvent;
import net.labymod.api.event.client.scoreboard.ScoreboardTeamUpdateEvent;
import net.labymod.api.util.I18n;
import org.jetbrains.annotations.Nullable;

/**
 * Detects the GrieferGames sub-server from scoreboard and tab-list text once the host check has
 * already marked the client as being on GrieferGames.
 */
public class GGScoreboardListener {

  private final GrieferGames griefergames;

  private String currentRegion = null;
  private CloudRegionType currentRegionType = null;
  private int sidebarPollCooldown;
  private int presenceRefreshCooldown;
  private int debugDumpCooldown;

  public GGScoreboardListener(GrieferGames griefergames) {
    this.griefergames = griefergames;
  }

  @Subscribe
  public void onTick(GameTickEvent event) {
    if (event.phase() != Phase.POST) {
      return;
    }
    if (!griefergames.configuration().enabled().get()) {
      return;
    }
    if (!griefergames.state().isOnGrieferGames()) {
      if (this.presenceRefreshCooldown-- <= 0) {
        this.presenceRefreshCooldown = 20;
        GrieferGamesPresence.refreshFromCurrentServer(griefergames);
      }
      if (!griefergames.state().isOnGrieferGames()) {
        return;
      }
    }
    if (griefergames.state().isNetworkKnown() && !griefergames.state().getSubServer().isBlank()) {
      return;
    }
    pollWhileUnknown();
    if (this.debugDumpCooldown-- <= 0) {
      this.debugDumpCooldown = 100;
      dumpDetectionDebug();
    }
  }

  @Subscribe
  public void onScoreboardTeams(ScoreboardTeamUpdateEvent event) {
    if (!griefergames.configuration().enabled().get()
        || !griefergames.state().isOnGrieferGames()) {
      return;
    }
    if (event.team().getTeamName() == null) {
      return;
    }
    String teamName = event.team().getTeamName();

    Scoreboard scoreboard = Laby.labyAPI().minecraft().getScoreboard();
    if (scoreboard != null && applyLegacyFromScoreboard(scoreboard)) {
      // updated via paired title/value or classic server_value
    } else if ("server_value".equals(teamName)) {
      applyLegacyTeam(event.team());
    }

    if (teamName.trim().equalsIgnoreCase("money_value")
        && event.team().getPrefix() != null
        && griefergames.state().getSubServerType() == SubServerType.CLOUD) {
      if (currentRegionType == null) {
        return;
      }
      if (currentRegionType == CloudRegionType.MINIGAME) {
        String minigameName = getNameFromTeamComponent(event.team().getPrefix());
        if (minigameName != null && !griefergames.state().getSubServer().equals(minigameName)) {
          griefergames.state().setSubServer(minigameName);
          Laby.labyAPI().eventBus().fire(new GGSubServerChangeEvent(minigameName));
        }
      } else if (currentRegionType == CloudRegionType.EVENT) {
        String eventName = getNameFromTeamComponent(event.team().getPrefix());
        if (eventName != null && !griefergames.state().getSubServer().equals(eventName)) {
          griefergames.state().setSubServer(eventName);
          Laby.labyAPI().eventBus().fire(new GGSubServerChangeEvent(eventName));
        }
      }
    }
  }

  @Subscribe
  public void onTablistUpdate(PlayerListUpdateEvent event) {
    if (!griefergames.configuration().enabled().get()
        || !griefergames.state().isOnGrieferGames()) {
      return;
    }
    if (applyLegacyFromTabHeader()) {
      return;
    }
    applyCloudFromTabList();
  }

  private void pollWhileUnknown() {
    if (applyLegacyFromTabHeader()) {
      return;
    }
    Scoreboard scoreboard = Laby.labyAPI().minecraft().getScoreboard();
    if (scoreboard != null) {
      if (applyLegacyFromScoreboard(scoreboard)) {
        return;
      }
      if (this.sidebarPollCooldown-- <= 0) {
        this.sidebarPollCooldown = 20;
        if (applyLegacyFromSidebar(scoreboard)) {
          return;
        }
      }
    }
    applyCloudFromTabList();
  }

  private boolean applyLegacyFromTabHeader() {
    TabList tablist = Laby.labyAPI().minecraft().getTabList();
    if (tablist == null || tablist.header() == null) {
      return false;
    }
    String header = plain(tablist.header());
    Optional<String> legacy = NetworkDetector.legacyFromTabHeader(header);
    if (legacy.isEmpty()) {
      return false;
    }
    applyLegacy(legacy.get());
    return true;
  }

  private boolean applyLegacyFromScoreboard(Scoreboard scoreboard) {
    java.util.HashMap<String, ScoreboardTeam> byName = new java.util.HashMap<>();
    for (ScoreboardTeam team : scoreboard.getTeams()) {
      byName.put(team.getTeamName(), team);
    }
    for (ScoreboardTeam team : byName.values()) {
      Optional<String> base = NetworkDetector.valueBase(team.getTeamName());
      String pairedTitle = null;
      if (base.isPresent()) {
        ScoreboardTeam titleTeam = byName.get(base.get() + "_title");
        if (titleTeam != null) {
          pairedTitle = plain(titleTeam.getPrefix()) + plain(titleTeam.getSuffix());
        }
      }
      if (applyLegacyTeam(team, pairedTitle)) {
        return true;
      }
    }
    return false;
  }

  /**
   * @return true when the legacy network was applied from this team
   */
  private boolean applyLegacyTeam(ScoreboardTeam team) {
    return applyLegacyTeam(team, null);
  }

  private boolean applyLegacyTeam(ScoreboardTeam team, @Nullable String pairedTitlePlainText) {
    Optional<String> legacySubServer = NetworkDetector.legacySubServer(
        team.getTeamName(),
        teamDisplayCandidates(team),
        pairedTitlePlainText);
    if (legacySubServer.isEmpty()) {
      return false;
    }
    applyLegacy(legacySubServer.get());
    return true;
  }

  private boolean applyLegacyFromSidebar(Scoreboard scoreboard) {
    ScoreboardObjective objective = scoreboard.getObjective(DisplaySlot.SIDEBAR);
    if (objective == null) {
      return false;
    }
    for (ScoreboardScore score : scoreboard.getScores(objective)) {
      if (score == null || score.isHidden() || score.getName() == null) {
        continue;
      }
      ScoreboardTeam team = scoreboard.teamByEntry(score.getName());
      String line;
      if (team != null) {
        line = plain(team.getPrefix()) + score.getName() + plain(team.getSuffix());
      } else {
        line = score.getName();
      }
      Optional<String> legacy = NetworkDetector.legacySidebarLine(line);
      if (legacy.isPresent()) {
        applyLegacy(legacy.get());
        return true;
      }
    }
    return false;
  }

  private void applyLegacy(String subServerName) {
    NetworkTypeUpdater.apply(griefergames, SubServerType.REGULAR);
    if (!griefergames.state().getSubServer().equals(subServerName)) {
      griefergames.state().setSubServer(subServerName);
      Laby.labyAPI().eventBus().fire(new GGSubServerChangeEvent(subServerName));
    }
  }

  private List<String> teamDisplayCandidates(ScoreboardTeam team) {
    String prefix = plain(team.getPrefix());
    String suffix = plain(team.getSuffix());
    List<String> candidates = new ArrayList<>(8);
    candidates.add(prefix);
    candidates.add(suffix);
    candidates.add(prefix + suffix);
    for (String entry : team.getEntries()) {
      if (entry == null || entry.isBlank()) {
        continue;
      }
      candidates.add(prefix + entry + suffix);
      candidates.add(entry);
    }
    return candidates;
  }

  private String plain(Component component) {
    if (component == null) {
      return "";
    }
    return griefergames.helper().componentToPlainText(component);
  }

  private void applyCloudFromTabList() {
    TabList tablist = Laby.labyAPI().minecraft().getTabList();
    if (tablist == null) {
      return;
    }
    Component header = tablist.header();
    if (header == null) {
      return;
    }
    String headerText = griefergames.helper().componentToPlainText(header);
    Optional<NetworkDetector.CloudServer> cloudServer = NetworkDetector.cloudServer(headerText);
    if (cloudServer.isEmpty()) {
      return;
    }
    CloudRegionType regionType = cloudServer.get().regionType();
    String serverName = cloudServer.get().serverName();
    NetworkTypeUpdater.apply(griefergames, SubServerType.CLOUD);
    boolean skipUpdate = (currentRegionType == CloudRegionType.MINIGAME || regionType == CloudRegionType.EVENT)
        && regionType == currentRegionType;
    currentRegion = serverName;
    currentRegionType = regionType;
    String subServerName = I18n.translate("griefergames.region_type.with_name." + regionType.name().toLowerCase(),
        regionType.onlyName(serverName)
    );
    if (!skipUpdate && !griefergames.state().getSubServer().equals(subServerName)) {
      griefergames.state().setSubServer(subServerName);
      Laby.labyAPI().eventBus().fire(new GGSubServerChangeEvent(subServerName));
    }
  }

  private String getNameFromTeamComponent(Component component) {
    try {
      if (component.getChildren().size() > 0) {
        return getNameFromTeamComponent(component.getChildren().get(0));
      }
      if (component instanceof TextComponent) {
        return ((TextComponent) component).getText().trim();
      }
      return null;
    } catch (Exception ignored) {
      return null;
    }
  }

  private void dumpDetectionDebug() {
    if (!Laby.labyAPI().labyModLoader().isAddonDevelopmentEnvironment()) {
      return;
    }
    StringBuilder out = new StringBuilder(GrieferGames.LOG_PREFIX)
        .append("detect onGG=").append(griefergames.state().isOnGrieferGames())
        .append(" type=").append(griefergames.state().getSubServerType())
        .append(" sub=").append(griefergames.state().getSubServer());
    Scoreboard scoreboard = Laby.labyAPI().minecraft().getScoreboard();
    if (scoreboard == null) {
      out.append(" scoreboard=null");
      griefergames.logger().info(out.toString());
      return;
    }
    out.append(" teams=[");
    boolean first = true;
    for (ScoreboardTeam team : scoreboard.getTeams()) {
      if (!first) {
        out.append(" | ");
      }
      first = false;
      out.append(team.getTeamName())
          .append("{p='").append(plain(team.getPrefix()))
          .append("' s='").append(plain(team.getSuffix()))
          .append("' e=").append(team.getEntries())
          .append("}");
    }
    out.append(']');
    ScoreboardObjective objective = scoreboard.getObjective(DisplaySlot.SIDEBAR);
    if (objective != null) {
      out.append(" sidebar='").append(plain(objective.getTitle())).append("' scores=[");
      first = true;
      for (ScoreboardScore score : scoreboard.getScores(objective)) {
        if (score == null || score.isHidden()) {
          continue;
        }
        if (!first) {
          out.append(" | ");
        }
        first = false;
        ScoreboardTeam team = scoreboard.teamByEntry(score.getName());
        String line = team == null
            ? score.getName()
            : plain(team.getPrefix()) + score.getName() + plain(team.getSuffix());
        out.append(score.getName()).append("=>'").append(line).append("'");
      }
      out.append(']');
    }
    TabList tablist = Laby.labyAPI().minecraft().getTabList();
    if (tablist != null && tablist.header() != null) {
      out.append(" tab='").append(plain(tablist.header())).append("'");
    }
    griefergames.logger().info(out.toString());
  }

}
