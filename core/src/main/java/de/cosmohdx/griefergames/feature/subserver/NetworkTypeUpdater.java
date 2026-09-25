package de.cosmohdx.griefergames.feature.subserver;

import de.cosmohdx.griefergames.GrieferGames;
import de.cosmohdx.griefergames.core.SubServerType;
import java.util.Optional;
import net.labymod.api.Laby;

/**
 * Writes {@link SubServerType} and fires {@link GGNetworkTypeChangeEvent} only when it changes.
 */
public final class NetworkTypeUpdater {

  private NetworkTypeUpdater() {
  }

  /**
   * The event to fire when the network type actually changes.
   * The same value, or a missing next type, does not produce an event.
   */
  public static Optional<GGNetworkTypeChangeEvent> change(SubServerType current, SubServerType next) {
    if (next == null || current == next) {
      return Optional.empty();
    }
    return Optional.of(new GGNetworkTypeChangeEvent(current, next));
  }

  public static void apply(GrieferGames griefergames, SubServerType next) {
    change(griefergames.state().getSubServerType(), next).ifPresent(event -> {
      griefergames.state().setSubServerType(event.newType());
      Laby.labyAPI().eventBus().fire(event);
    });
  }
}
