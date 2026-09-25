package de.cosmohdx.griefergames.v26_2;

import de.cosmohdx.griefergames.GrieferGames;
import de.cosmohdx.griefergames.core.GrieferGamesConfig;

/** Safe config access while early Minecraft renderers are loading. */
public final class TooltipFeatures {
  private TooltipFeatures() {}
  private static GrieferGamesConfig config() {
    GrieferGames addon = GrieferGames.get();
    return addon == null ? null : addon.configuration();
  }
  public static boolean mapTooltipPreviewEnabled() {
    var config = config();
    return config != null && config.enabled().get() && config.mapTooltipPreview().get();
  }
  public static boolean headTooltipPreviewEnabled() {
    var config = config();
    return config != null && config.enabled().get() && config.headTooltipPreview().get();
  }
  public static boolean headEnchantmentGlintEnabled() {
    var config = config();
    return config != null && config.enabled().get() && config.headEnchantmentGlint().get();
  }
  public static boolean overstackingFixEnabled() {
    var config = config();
    return config != null && config.enabled().get() && config.overstackingFix().get();
  }
}
