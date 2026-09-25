package de.cosmohdx.griefergames.feature.delay;

import de.cosmohdx.griefergames.GrieferGames;
import de.cosmohdx.griefergames.feature.subserver.GGSubServerChangeEvent;
import net.labymod.api.event.Subscribe;
import java.util.concurrent.TimeUnit;

public class DelaySubServerListener {

  private final GrieferGames griefergames;

  public DelaySubServerListener(GrieferGames griefergames) {
    this.griefergames = griefergames;
  }

  @Subscribe
  public void onSubServerChange(GGSubServerChangeEvent event) {
    String subServerName = event.subServerName();
    if (griefergames.helper().isCityBuild(subServerName)) {
      if (!griefergames.state().isCitybuildDelay()) {
        griefergames.state().setWaitTime(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(15));
      }
      griefergames.state().setCitybuildDelay(false);
    } else if (subServerName.equals("portal")) {
      if (!griefergames.state().isCitybuildDelay()) {
        griefergames.state().setWaitTime(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(12));
      }
    } else if (subServerName.equals("skyblock")) {
      if (!griefergames.state().isCitybuildDelay()) {
        griefergames.state().setWaitTime(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(15));
      }
    }
  }
}
