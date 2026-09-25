package de.cosmohdx.griefergames.core;

import java.util.UUID;

/**
 * A player entity currently loaded on the client, reduced to what the nearby-player list needs.
 * No direction and no coordinates: only the distance in blocks.
 */
public record LoadedPlayerView(
    UUID uniqueId,
    String name,
    double distance,
    boolean invisible,
    boolean spectator,
    boolean lineOfSight) {
}
