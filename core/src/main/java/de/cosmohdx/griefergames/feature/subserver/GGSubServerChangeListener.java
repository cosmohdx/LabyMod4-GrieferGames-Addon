package de.cosmohdx.griefergames.feature.subserver;

import de.cosmohdx.griefergames.GrieferGames;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.NamedTextColor;
import net.labymod.api.event.Subscribe;
import net.labymod.api.util.I18n;

public class GGSubServerChangeListener {

  private final GrieferGames griefergames;

  public GGSubServerChangeListener(GrieferGames griefergames) {
    this.griefergames = griefergames;
  }

  @Subscribe
  public void onSubServerChange(GGSubServerChangeEvent event) {
    if (!griefergames.helper().isCityBuild(event.subServerName())) return;
    if (!griefergames.configuration().automations().isSendSubServerEnabled()) return;

    String formattedServerName = griefergames.helper().formatServerName(event.subServerName());
    griefergames.displayAddonMessage(Component.text(
        I18n.translate(griefergames.namespace() + ".messages.citybuildJoin")
            .replace("{citybuild}", formattedServerName),
        NamedTextColor.GRAY
    ));
  }
}
