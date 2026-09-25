package de.cosmohdx.griefergames.feature.booster;

import de.cosmohdx.griefergames.GrieferGames;
import de.cosmohdx.griefergames.core.SubServerType;
import de.cosmohdx.griefergames.feature.subserver.GGSubServerChangeEvent;
import net.labymod.api.event.Phase;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.chat.ChatReceiveEvent;
import net.labymod.api.event.client.lifecycle.GameTickEvent;

public class BoosterListener {

  private final GrieferGames griefergames;

  public BoosterListener(GrieferGames griefergames) {
    this.griefergames = griefergames;
  }

  @Subscribe
  public void onTick(GameTickEvent event) {
    if (!griefergames.state().isOnGrieferGames()) return;
    if (event.phase() != Phase.POST) return;
    if (griefergames.state().getSubServerType() != SubServerType.REGULAR) return;
    if (!griefergames.configuration().automations().boosterConfig().isEnabled()) return;
    if (griefergames.configuration().automations().boosterConfig().isHideBoosterMenu() || griefergames.state().isHideBoosterMenu()) {
      if (griefergames.controller().hideBoosterMenu()) {
        griefergames.state().setHideBoosterMenu(false);
      }
    }
  }

  @Subscribe
  public void onMessage(ChatReceiveEvent event) {
    if (!griefergames.state().isOnGrieferGames()) return;
    if (griefergames.state().getSubServerType() != SubServerType.REGULAR) return;
    if (!event.chatMessage().getPlainText().equals("[Switcher] Daten heruntergeladen!")) return;
    if (griefergames.configuration().automations().boosterConfig().loadBoostersOnJoin()) {
      griefergames.state().setHideBoosterMenu(true);
      griefergames.sendMessage("/booster");
    }
  }

  @Subscribe
  public void onSubServerChange(GGSubServerChangeEvent event) {
    griefergames.boosterController().resetBoosters();
  }
}
