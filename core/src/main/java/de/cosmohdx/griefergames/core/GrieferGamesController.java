package de.cosmohdx.griefergames.core;

import java.util.List;
import net.labymod.api.reference.annotation.Referenceable;
import org.jetbrains.annotations.Nullable;

@Nullable
@Referenceable
public abstract class GrieferGamesController {
  public abstract boolean playerAllowedFlying();

  public abstract boolean hideBoosterMenu();

  /**
   * Players loaded in the client world within {@code radius} blocks of the view entity.
   * The local player and the current camera entity are left out. Distance is the straight
   * line between entity positions. Line of sight is a clear ray from the view entity's eyes
   * to the target's head or body center. Blocks with a collision shape block that ray.
   * An empty world returns an empty list.
   */
  public abstract List<LoadedPlayerView> loadedPlayersWithin(double radius);
}
