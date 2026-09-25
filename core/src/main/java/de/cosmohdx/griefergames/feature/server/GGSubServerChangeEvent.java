package de.cosmohdx.griefergames.feature.server;

import net.labymod.api.event.Event;

public class GGSubServerChangeEvent implements Event {
  private String subServerName;

  public GGSubServerChangeEvent(String subServerName) {
    this.subServerName = subServerName;
  }

  public String subServerName() {
    return subServerName;
  }
}
