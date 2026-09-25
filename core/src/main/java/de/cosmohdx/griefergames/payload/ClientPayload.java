package de.cosmohdx.griefergames.payload;

/**
 * Typed model of a message the GrieferGames server (or MysteryMod) sent to the client.
 * Features use these types and never read the plugin-message bytes themselves.
 */
public interface ClientPayload {

  /**
   * Wire id. On {@code griefergames:main} this is the leading modified-UTF string.
   * On {@code mysterymod:mm} this is the message key.
   */
  String id();
}
