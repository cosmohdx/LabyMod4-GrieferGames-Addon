package de.cosmohdx.griefergames.payload;

import de.cosmohdx.griefergames.GrieferGames;
import de.cosmohdx.griefergames.core.GrieferGamesConfig;
import net.labymod.api.Laby;

/**
 * Writes payload traces when the switch under Entwicklung is on.
 * Release clients never log, even if a copied config file still contains {@code true}.
 */
public final class PayloadDebug {

  private PayloadDebug() {
  }

  public static void log(GrieferGames addon, String message) {
    if (!active(addon)) {
      return;
    }
    addon.logger().info(GrieferGames.LOG_PREFIX + "Payload " + message);
  }

  public static boolean active(GrieferGames addon) {
    if (addon == null || !Laby.labyAPI().labyModLoader().isAddonDevelopmentEnvironment()) {
      return false;
    }
    GrieferGamesConfig config = addon.configuration();
    return config != null && config.dev().logPayloads();
  }
}
