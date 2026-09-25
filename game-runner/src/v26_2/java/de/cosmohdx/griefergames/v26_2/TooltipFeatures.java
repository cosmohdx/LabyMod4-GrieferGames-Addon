package de.cosmohdx.griefergames.v26_2;

import de.cosmohdx.griefergames.GrieferGames;
import de.cosmohdx.griefergames.core.GrieferGamesConfig;
import java.lang.reflect.Method;

/** Safe config access while early Minecraft renderers are loading. */
public final class TooltipFeatures {
  private TooltipFeatures() {}
  private static GrieferGamesConfig config() {
    GrieferGames addon = GrieferGames.get();
    return addon == null ? null : addon.configuration();
  }
  private static boolean syntaxEnabled(String methodName) {
    try {
      Class<?> syntax = Class.forName("de.kilian.syntax.core.SyntaxAddon");
      Method method = syntax.getMethod(methodName);
      return Boolean.TRUE.equals(method.invoke(null));
    } catch (ReflectiveOperationException | LinkageError ignored) {
      return false;
    }
  }
  public static boolean mapTooltipPreviewEnabled() {
    var config = config();
    return config != null && config.enabled().get() && config.mapTooltipPreview().get()
        && !syntaxEnabled("mapTooltipPreviewEnabled");
  }
  public static boolean headTooltipPreviewEnabled() {
    var config = config();
    return config != null && config.enabled().get() && config.headTooltipPreview().get()
        && !syntaxEnabled("headTooltipPreviewEnabled");
  }
  public static boolean headEnchantmentGlintEnabled() {
    var config = config();
    return config != null && config.enabled().get() && config.headEnchantmentGlint().get()
        && !syntaxEnabled("headEnchantmentGlintEnabled");
  }
  public static boolean overstackingFixEnabled() {
    var config = config();
    return config != null && config.enabled().get() && config.overstackingFix().get()
        && !syntaxEnabled("overstackCountEnabled") && !syntaxEnabled("homeAxePlotCountEnabled");
  }
}
