package de.cosmohdx.griefergames.core;

import net.labymod.api.client.chat.advanced.IngameChatTab;
import org.jetbrains.annotations.Nullable;

public class AddonState {

  private boolean onGrieferGames;
  private String subServer = "";
  private SubServerType subServerType = SubServerType.UNKNOWN;
  @Nullable
  private IngameChatTab secondChat;

  @Nullable
  private String nickname;
  private double income;
  private boolean afk;
  private long lastActivity;

  private long waitTime;
  private boolean citybuildDelay;
  private boolean hideBoosterMenu;
  private boolean redstoneActive;

  public boolean isOnGrieferGames() {
    return onGrieferGames;
  }

  public void setOnGrieferGames(boolean onGrieferGames) {
    this.onGrieferGames = onGrieferGames;
  }

  public String getSubServer() {
    return subServer;
  }

  public void setSubServer(String subServer) {
    this.subServer = subServer;
  }

  public SubServerType getSubServerType() {
    return subServerType;
  }

  public void setSubServerType(SubServerType subServerType) {
    if (subServerType == null) {
      return;
    }
    this.subServerType = subServerType;
  }

  public boolean isSubServerType(SubServerType subServerType) {
    return this.subServerType == subServerType;
  }

  public boolean isLegacyNetwork() {
    return this.subServerType == SubServerType.REGULAR;
  }

  public boolean isCloudNetwork() {
    return this.subServerType == SubServerType.CLOUD;
  }

  public boolean isNetworkKnown() {
    return this.isLegacyNetwork() || this.isCloudNetwork();
  }

  @Nullable
  public IngameChatTab getSecondChat() {
    return secondChat;
  }

  public void setSecondChat(@Nullable IngameChatTab secondChat) {
    this.secondChat = secondChat;
  }

  @Nullable
  public String getNickname() {
    return nickname;
  }

  public void setNickname(@Nullable String nickname) {
    this.nickname = nickname;
  }

  public double getIncome() {
    return income;
  }

  public void setIncome(double income) {
    this.income = income;
  }

  public void addIncome(double income) {
    this.income += income;
  }

  public boolean isAfk() {
    return afk;
  }

  public void setAfk(boolean afk) {
    this.afk = afk;
  }

  public long getLastActivity() {
    return lastActivity;
  }

  public void setLastActivity(long lastActivity) {
    this.lastActivity = lastActivity;
  }

  public long getWaitTime() {
    return waitTime;
  }

  public void setWaitTime(long waitTime) {
    this.waitTime = waitTime;
  }

  public boolean isCitybuildDelay() {
    return citybuildDelay;
  }

  public void setCitybuildDelay(boolean citybuildDelay) {
    this.citybuildDelay = citybuildDelay;
  }

  public boolean isHideBoosterMenu() {
    return hideBoosterMenu;
  }

  public void setHideBoosterMenu(boolean hideBoosterMenu) {
    this.hideBoosterMenu = hideBoosterMenu;
  }

  public boolean isRedstoneActive() {
    return redstoneActive;
  }

  public void setRedstoneActive(boolean redstoneActive) {
    this.redstoneActive = redstoneActive;
  }
}
