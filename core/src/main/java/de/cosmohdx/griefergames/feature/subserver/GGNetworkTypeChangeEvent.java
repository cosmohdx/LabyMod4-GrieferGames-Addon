package de.cosmohdx.griefergames.feature.subserver;

import de.cosmohdx.griefergames.core.SubServerType;
import net.labymod.api.event.Event;

/**
 * Fired whenever the detected GrieferGames network changes, including the reset to
 * {@link SubServerType#UNKNOWN} on join and quit.
 */
public class GGNetworkTypeChangeEvent implements Event {

  private final SubServerType oldType;
  private final SubServerType newType;

  public GGNetworkTypeChangeEvent(SubServerType oldType, SubServerType newType) {
    this.oldType = oldType;
    this.newType = newType;
  }

  public SubServerType oldType() {
    return oldType;
  }

  public SubServerType newType() {
    return newType;
  }
}
