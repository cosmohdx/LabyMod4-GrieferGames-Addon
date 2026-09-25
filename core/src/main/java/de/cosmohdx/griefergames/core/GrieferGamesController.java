package de.cosmohdx.griefergames.core;

import net.labymod.api.reference.annotation.Referenceable;
import org.jetbrains.annotations.Nullable;

@Nullable
@Referenceable
public abstract class GrieferGamesController {
  public abstract boolean playerAllowedFlying();

  public abstract boolean hideBoosterMenu();

  /**
   * ARGB pixels of a filled map, or {@code null} when that map is not loaded on the client.
   */
  public int[] filledMapPixels(int mapId) {
    return null;
  }

  /**
   * {@code true} when this version's special item renderers ignore the enchantment foil flag.
   * The glint mixins no-op when this returns {@code false}.
   */
  public boolean patchSpecialItemEnchantmentGlint() {
    return false;
  }
}
