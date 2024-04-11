package de.neocraftr.griefergames.listener;

import de.neocraftr.griefergames.GrieferGames;
import de.neocraftr.griefergames.chat.events.GGSubServerChangeEvent;
import de.neocraftr.griefergames.enums.CloudRegionType;
import de.neocraftr.griefergames.enums.SubServerType;
import net.labymod.api.Laby;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.TextComponent;
import net.labymod.api.client.scoreboard.TabList;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.gui.screen.playerlist.PlayerListUpdateEvent;
import net.labymod.api.event.client.scoreboard.ScoreboardTeamUpdateEvent;
import net.labymod.api.util.I18n;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GGScoreboardListener {

  private final Pattern tabListServerRegex = Pattern.compile("Current Server: (.+?)-", Pattern.MULTILINE);

  private GrieferGames griefergames;

  private String currentRegion = null;
  private CloudRegionType currentRegionType = null;

  public GGScoreboardListener(GrieferGames griefergames) {
    this.griefergames = griefergames;
  }

  @Subscribe
  public void onScoreboardTeams(ScoreboardTeamUpdateEvent event) {
    if(!griefergames.isOnGrieferGames()) return;
    if(event.team().getTeamName() == null) return;

    if(event.team().getTeamName().equals("server_value")) {
      // Handle 1.8 Servers
      String subServerName = griefergames.helper().componentToPlainText(event.team().getPrefix()).toLowerCase();
      if(subServerName.isBlank() || subServerName.contains("lade")) return;

      griefergames.setSubServerType(SubServerType.REGULAR);
      if(!griefergames.getSubServer().equals(subServerName)) {
        griefergames.setSubServer(subServerName);
        GGSubServerChangeEvent changeEvent = new GGSubServerChangeEvent(subServerName);
        Laby.labyAPI().eventBus().fire(changeEvent);
      }
    } else if(event.team().getTeamName().trim().equalsIgnoreCase("money_value") &&
        event.team().getPrefix() != null && griefergames.getSubServerType() == SubServerType.CLOUD) {
      // Handle Cloud Minigame and Event Servers
      if(currentRegionType == null) return;
      if(currentRegionType == CloudRegionType.MINIGAME) {
        String minigameName = getNameFromTeamComponent(event.team().getPrefix());
        if(minigameName != null && !griefergames.getSubServer().equals(minigameName)) {
          griefergames.setSubServer(minigameName);
          GGSubServerChangeEvent changeEvent = new GGSubServerChangeEvent(minigameName);
          Laby.labyAPI().eventBus().fire(changeEvent);
        }
      }else if(currentRegionType == CloudRegionType.EVENT) {
        String eventName = getNameFromTeamComponent(event.team().getPrefix());
        if(eventName != null && !griefergames.getSubServer().equals(eventName)) {
          griefergames.setSubServer(eventName);
          GGSubServerChangeEvent changeEvent = new GGSubServerChangeEvent(eventName);
          Laby.labyAPI().eventBus().fire(changeEvent);
        }
      }
    }
  }

  @Subscribe
  public void onTablistUpdate(PlayerListUpdateEvent event) {
    TabList tablist = Laby.labyAPI().minecraft().getTabList();
    if(tablist == null) return;
    Component header = tablist.header();
    if(header == null) return;
    String serverName = CloudRegionType.extractServerNameFromComponent(header);
    if(serverName == null) return;
    CloudRegionType regionType = CloudRegionType.getRegionType(serverName);
    if(regionType != null) {
      griefergames.setSubServerType(SubServerType.CLOUD);
      boolean skipUpdate = (currentRegionType == CloudRegionType.MINIGAME || regionType == CloudRegionType.EVENT) && regionType == currentRegionType;
      currentRegion = serverName;
      currentRegionType = regionType;
      String subServerName = I18n.translate("griefergames.region_type.with_name." + regionType.name().toLowerCase(),
          regionType.onlyName(serverName)
      );
      if(!skipUpdate && !griefergames.getSubServer().equals(subServerName)) {
        griefergames.setSubServer(subServerName);
          GGSubServerChangeEvent changeEvent = new GGSubServerChangeEvent(subServerName);
          Laby.labyAPI().eventBus().fire(changeEvent);
      }
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
