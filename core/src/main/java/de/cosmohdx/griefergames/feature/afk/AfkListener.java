package de.cosmohdx.griefergames.feature.afk;

import de.cosmohdx.griefergames.GrieferGames;
import de.cosmohdx.griefergames.core.SubServerType;
import net.labymod.api.event.Phase;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.input.KeyEvent;
import net.labymod.api.event.client.input.KeyEvent.State;
import net.labymod.api.event.client.lifecycle.GameTickEvent;

public class AfkListener {

  private final GrieferGames griefergames;
  private final AfkActions actions;

  public AfkListener(GrieferGames griefergames) {
    this.griefergames = griefergames;
    this.actions = new AfkActions(griefergames);
  }

  @Subscribe
  public void onTick(GameTickEvent event) {
    if (!griefergames.state().isOnGrieferGames()) return;
    if (event.phase() != Phase.POST) return;
    if (griefergames.state().getSubServerType() != SubServerType.REGULAR
        && griefergames.state().getSubServerType() != SubServerType.CLOUD) {
      return;
    }
    if (!griefergames.state().isAfk()
        && griefergames.configuration().afk().isEnabled()
        && griefergames.state().getLastActivity() + (griefergames.configuration().afk().afkTimeMinutes() * 60000L) < System.currentTimeMillis()) {
      griefergames.state().setAfk(true);
      actions.perform(true);
    }
  }

  @Subscribe
  public void onKeyInput(KeyEvent event) {
    if (!griefergames.state().isOnGrieferGames()) return;
    if (griefergames.state().getSubServerType() != SubServerType.CLOUD
        && griefergames.state().getSubServerType() != SubServerType.REGULAR) {
      return;
    }
    if (event.state() != State.PRESS) return;

    griefergames.state().setLastActivity(System.currentTimeMillis());
    if (griefergames.state().isAfk()) {
      griefergames.state().setAfk(false);
      actions.perform(false);
    }
  }
}
