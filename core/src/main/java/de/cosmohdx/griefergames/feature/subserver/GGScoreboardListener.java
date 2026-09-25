package de.cosmohdx.griefergames.feature.subserver;

import de.cosmohdx.griefergames.GrieferGames;
import de.cosmohdx.griefergames.core.CloudRegionType;
import de.cosmohdx.griefergames.core.SubServerType;
import java.util.Optional;
import net.labymod.api.Laby;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.TextComponent;
import net.labymod.api.client.scoreboard.TabList;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.gui.screen.playerlist.PlayerListUpdateEvent;
import net.labymod.api.event.client.scoreboard.ScoreboardTeamUpdateEvent;
import net.labymod.api.util.I18n;

public class GGScoreboardListener {

  private GrieferGames griefergames;

  private String currentRegion = null;
  private CloudRegionType currentRegionType = null;

  public GGScoreboardListener(GrieferGames griefergames) {
    this.griefergames = griefergames;
  }

  @Subscribe
  public void onScoreboardTeams(ScoreboardTeamUpdateEvent event) {
    if(!griefergames.state().isOnGrieferGames()) return;
    if(event.team().getTeamName() == null) return;

    if(event.team().getTeamName().equals("server_value")) {
      // Handle 1.8 Servers
      String prefix = event.team().getPrefix() == null
          ? null
          : griefergames.helper().componentToPlainText(event.team().getPrefix());
      Optional<String> legacySubServer = NetworkDetector.legacySubServer(event.team().getTeamName(), prefix);
      if(legacySubServer.isEmpty()) return;

      NetworkTypeUpdater.apply(griefergames, SubServerType.REGULAR);
      String subServerName = legacySubServer.get();
      if(!griefergames.state().getSubServer().equals(subServerName)) {
        griefergames.state().setSubServer(subServerName);
        GGSubServerChangeEvent changeEvent = new GGSubServerChangeEvent(subServerName);
        Laby.labyAPI().eventBus().fire(changeEvent);
      }
    } else if(event.team().getTeamName().trim().equalsIgnoreCase("money_value") &&
        event.team().getPrefix() != null && griefergames.state().getSubServerType() == SubServerType.CLOUD) {
      // Handle Cloud Minigame and Event Servers
      if(currentRegionType == null) return;
      if(currentRegionType == CloudRegionType.MINIGAME) {
        String minigameName = getNameFromTeamComponent(event.team().getPrefix());
        if(minigameName != null && !griefergames.state().getSubServer().equals(minigameName)) {
          griefergames.state().setSubServer(minigameName);
          GGSubServerChangeEvent changeEvent = new GGSubServerChangeEvent(minigameName);
          Laby.labyAPI().eventBus().fire(changeEvent);
        }
      }else if(currentRegionType == CloudRegionType.EVENT) {
        String eventName = getNameFromTeamComponent(event.team().getPrefix());
        if(eventName != null && !griefergames.state().getSubServer().equals(eventName)) {
          griefergames.state().setSubServer(eventName);
          GGSubServerChangeEvent changeEvent = new GGSubServerChangeEvent(eventName);
          Laby.labyAPI().eventBus().fire(changeEvent);
        }
      }
    }
  }

  @Subscribe
  public void onTablistUpdate(PlayerListUpdateEvent event) {
    if(!griefergames.state().isOnGrieferGames()) return;
    TabList tablist = Laby.labyAPI().minecraft().getTabList();
    if(tablist == null) return;
    Component header = tablist.header();
    if(header == null) return;
    String headerText = griefergames.helper().componentToPlainText(header);
    Optional<NetworkDetector.CloudServer> cloudServer = NetworkDetector.cloudServer(headerText);
    if(cloudServer.isEmpty()) return;
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
    if(!skipUpdate && !griefergames.state().getSubServer().equals(subServerName)) {
      griefergames.state().setSubServer(subServerName);
      GGSubServerChangeEvent changeEvent = new GGSubServerChangeEvent(subServerName);
      Laby.labyAPI().eventBus().fire(changeEvent);
    }
  }

  /**
   * Fetches the name from the team component of the scorebaord
   * @param component Component
   * @return name
   */
  private String getNameFromTeamComponent(Component component) {
    try {
      if (component.getChildren().size() > 0) {
        return getNameFromTeamComponent(component.getChildren().get(0));
      }
      if (component instanceof TextComponent) {
        //@TODO Check if color is possible
        return ((TextComponent) component).getText().trim();
      }
      return null;
    }catch (Exception ignored) {
      return null;
    }
  }

}
