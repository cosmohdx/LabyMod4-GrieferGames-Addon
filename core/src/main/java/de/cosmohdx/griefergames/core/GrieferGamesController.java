package de.cosmohdx.griefergames.core;

import net.labymod.api.reference.annotation.Referenceable;
import org.jetbrains.annotations.Nullable;

@Nullable
@Referenceable
public abstract class GrieferGamesController {
  public abstract boolean playerAllowedFlying();

  public abstract boolean hideBoosterMenu();
}
