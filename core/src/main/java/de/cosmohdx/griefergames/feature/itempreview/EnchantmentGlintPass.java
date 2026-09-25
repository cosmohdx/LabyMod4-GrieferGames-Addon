package de.cosmohdx.griefergames.feature.itempreview;

import de.cosmohdx.griefergames.GrieferGames;

/**
 * Set while an enchanted skull, banner or chest item is rendered.
 * Block renderers read it and must not keep the flag after the item render returns.
 */
public final class EnchantmentGlintPass {

  private static final ThreadLocal<Boolean> REQUESTED = ThreadLocal.withInitial(() -> Boolean.FALSE);

  private EnchantmentGlintPass() {
  }

  public static boolean enabled() {
    GrieferGames addon = GrieferGames.get();
    if (addon == null || !addon.configuration().enabled().get()) {
      return false;
    }
    if (!addon.configuration().itemPreview().enchantmentGlint()) {
      return false;
    }
    return addon.controller() != null && addon.controller().patchSpecialItemEnchantmentGlint();
  }

  public static void request(boolean foil) {
    REQUESTED.set(foil && enabled());
  }

  public static boolean requested() {
    return Boolean.TRUE.equals(REQUESTED.get());
  }

  public static void clear() {
    REQUESTED.set(Boolean.FALSE);
  }
}
