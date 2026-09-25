package de.cosmohdx.griefergames.core.config;

import de.cosmohdx.griefergames.core.GrieferGamesConfig;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.labymod.config.ConfigurationVersionUpdateEvent;

public class ConfigMigrationListener {

  @Subscribe
  public void onConfigVersionUpdate(ConfigurationVersionUpdateEvent event) {
    if (event.getConfigClass() != GrieferGamesConfig.class) {
      return;
    }
    GrieferGamesConfigMigration.migrate(event.getJsonObject(), event.getUsedVersion());
    event.setJsonObject(event.getJsonObject());
  }
}
