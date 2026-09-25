package de.cosmohdx.griefergames.feature.automation;

import de.cosmohdx.griefergames.GrieferGames;
import de.cosmohdx.griefergames.core.SubServerType;
import net.labymod.api.event.Phase;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.lifecycle.GameTickEvent;

public class GGTickListener {
  private GrieferGames griefergames;

  public GGTickListener(GrieferGames griefergames) {
    this.griefergames = griefergames;
  }

  @Subscribe
  public void onTick(GameTickEvent event) {
    if(!griefergames.state().isOnGrieferGames()) return;
    if(event.phase() == Phase.POST) {
      if (griefergames.state().getSubServerType() == SubServerType.REGULAR || griefergames.state().getSubServerType() == SubServerType.CLOUD) {
        long now = System.currentTimeMillis();
        if(!griefergames.state().isAfk() && griefergames.state().getLastActivity() + (griefergames.configuration().automations().afkConfig().afkTime().get() * 60000) < System.currentTimeMillis()
          && griefergames.configuration().automations().afkConfig().isEnabled()) {
          griefergames.state().setAfk(true);
          griefergames.helper().performAfkActions(true);
        }
      }
      if(griefergames.state().getSubServerType() == SubServerType.REGULAR) {
        if(griefergames.configuration().automations().boosterConfig().isEnabled()) {
          if(griefergames.configuration().automations().boosterConfig().isHideBoosterMenu() || griefergames.state().isHideBoosterMenu()) {
            if(griefergames.controller().hideBoosterMenu()) {
              griefergames.state().setHideBoosterMenu(false);
            }
          }
        }
      }
    }
  }
}
