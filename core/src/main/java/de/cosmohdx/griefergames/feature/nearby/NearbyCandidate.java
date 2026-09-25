package de.cosmohdx.griefergames.feature.nearby;

import java.util.UUID;
import net.labymod.api.client.component.Component;
import org.jetbrains.annotations.Nullable;

/**
 * One loaded player before filtering. Flags come from the client world and the tab list.
 */
public record NearbyCandidate(
    UUID id,
    String name,
    @Nullable Component displayName,
    double distance,
    boolean self,
    boolean invisible,
    boolean spectator,
    boolean listed,
    boolean lineOfSight) {
}
