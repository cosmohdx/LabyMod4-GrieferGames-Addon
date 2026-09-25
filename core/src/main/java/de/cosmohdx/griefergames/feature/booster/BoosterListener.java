package de.cosmohdx.griefergames.feature.booster;

import de.cosmohdx.griefergames.GrieferGames;
import de.cosmohdx.griefergames.core.SubServerType;
import de.cosmohdx.griefergames.feature.subserver.GGSubServerChangeEvent;
import de.cosmohdx.griefergames.payload.model.BoosterPayload;
import net.labymod.api.event.Phase;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.chat.ChatReceiveEvent;
import net.labymod.api.event.client.lifecycle.GameTickEvent;
import net.labymod.api.event.client.network.server.ServerDisconnectEvent;

public class BoosterListener {

  private static final int PAYLOAD_WAIT_TICKS = 40;

  private final GrieferGames griefergames;
  private boolean boosterPayloadSeen;
  private int ticksUntilBoosterMenu = -1;

  public BoosterListener(GrieferGames griefergames) {
    this.griefergames = griefergames;
    griefergames.payloads().subscribe(BoosterPayload.class, this::onBoosterPayload);
  }

  private void onBoosterPayload(BoosterPayload payload) {
    this.griefergames.boosterController().applyPayload(payload);
    this.boosterPayloadSeen = true;
    this.ticksUntilBoosterMenu = -1;
  }

  @Subscribe
  public void onTick(GameTickEvent event) {
    if (!griefergames.state().isOnGrieferGames()) return;
    if (event.phase() != Phase.POST) return;
    if (this.ticksUntilBoosterMenu > 0) {
      this.ticksUntilBoosterMenu--;
      if (this.ticksUntilBoosterMenu == 0 && !this.boosterPayloadSeen) {
        this.openBoosterMenu();
      }
    }
    if (griefergames.state().getSubServerType() != SubServerType.REGULAR) return;
    if (!griefergames.configuration().booster().isEnabled()) return;
    if (griefergames.configuration().booster().hideBoosterMenu() || griefergames.state().isHideBoosterMenu()) {
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
    if (!griefergames.configuration().booster().loadBoostersOnJoin() || this.boosterPayloadSeen) return;
    this.ticksUntilBoosterMenu = PAYLOAD_WAIT_TICKS;
  }

  @Subscribe
  public void onSubServerChange(GGSubServerChangeEvent event) {
    griefergames.boosterController().resetBoosters();
  }

  @Subscribe
  public void onServerQuit(ServerDisconnectEvent event) {
    this.cancelBoosterMenu();
  }

  private void openBoosterMenu() {
    this.ticksUntilBoosterMenu = -1;
    if (this.boosterPayloadSeen
        || !this.griefergames.state().isOnGrieferGames()
        || this.griefergames.state().getSubServerType() != SubServerType.REGULAR
        || !this.griefergames.configuration().booster().loadBoostersOnJoin()) {
      return;
    }
    this.griefergames.state().setHideBoosterMenu(true);
    this.griefergames.sendMessage("/booster");
  }

  private void cancelBoosterMenu() {
    this.boosterPayloadSeen = false;
    this.ticksUntilBoosterMenu = -1;
  }
}
