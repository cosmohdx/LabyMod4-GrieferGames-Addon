package de.cosmohdx.griefergames.feature.nearby;

import java.util.UUID;
import net.labymod.api.client.component.Component;
import org.jetbrains.annotations.Nullable;

/**
 * A player shown in the nearby list. The name is the tab-list name, nicks included.
 * {@code distance} is only used to pick a rough step. It is not a coordinate.
 */
public record NearbyPlayer(
    UUID id,
    String name,
    @Nullable Component displayName,
    double distance) {
}
