package de.cosmohdx.griefergames.feature.automation;

import de.cosmohdx.griefergames.GrieferGames;
import de.cosmohdx.griefergames.feature.subserver.GGSubServerChangeEvent;
import net.labymod.api.event.Subscribe;
import java.util.concurrent.TimeUnit;

public class AutoPortalListener {

  private final GrieferGames griefergames;

  public AutoPortalListener(GrieferGames griefergames) {
    this.griefergames = griefergames;
  }

  @Subscribe
  public void onSubServerChange(GGSubServerChangeEvent event) {
    if (event.subServerName().equals("lobby") && griefergames.configuration().automations().isAutoPortalEnabled()) {
      griefergames.schedule(() -> griefergames.sendMessage("/portal"), 500, TimeUnit.MILLISECONDS);
    }
  }
}
