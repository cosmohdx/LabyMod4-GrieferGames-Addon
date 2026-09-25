package de.cosmohdx.griefergames.feature.nearby;

import de.cosmohdx.griefergames.core.CloudRegionType;
import de.cosmohdx.griefergames.core.Helper;
import de.cosmohdx.griefergames.core.SubServerType;
import java.util.Locale;
import org.jetbrains.annotations.Nullable;

/**
 * Decides whether the nearby-player list may run on the current server.
 * An unknown network never passes. With the citybuild switch on, farm worlds, the event
 * server and minigames stay off: {@link Helper#isCityBuild(String)} also matches those on 1.8.
 */
public final class NearbyServerGate {

  private NearbyServerGate() {
  }

  public static boolean allows(
      boolean onlyOnCitybuild,
      @Nullable SubServerType network,
      @Nullable String subServer,
      @Nullable CloudRegionType cloudRegion) {
    if (network != SubServerType.REGULAR && network != SubServerType.CLOUD) {
      return false;
    }
    if (!onlyOnCitybuild) {
      return true;
    }
    if (network == SubServerType.CLOUD) {
      return cloudRegion == CloudRegionType.CITYBUILD;
    }
    return isLegacyCitybuild(subServer);
  }

  public static boolean isLegacyCitybuild(@Nullable String subServer) {
    if (subServer == null) {
      return false;
    }
    String name = subServer.trim().toLowerCase(Locale.ROOT);
    if (name.isEmpty()
        || name.equals("lava")
        || name.equals("wasser")
        || name.equals("event")
        || name.equals("cb0")
        || name.startsWith("farmworld")
        || name.startsWith("farmwelt")
        || name.startsWith("farm-")) {
      return false;
    }
    if (name.equals("extreme") || name.equals("evil") || name.equals("nature") || name.equals("cbe")) {
      return true;
    }
    if (name.startsWith("citybuild")) {
      return true;
    }
    return name.startsWith("cb");
  }
}
