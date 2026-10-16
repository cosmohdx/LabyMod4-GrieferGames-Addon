package de.cosmohdx.griefergames.feature.chat.search;

/**
 * Decides whether a displayed chat line is written to the session log.
 * Sent lines are not special: the server echo is a normal received message.
 */
public final class ChatSearchGate {

  private ChatSearchGate() {
  }

  public static boolean shouldRecord(
      boolean addonEnabled,
      boolean chatEnabled,
      boolean featureEnabled,
      boolean onlyOnGrieferGames,
      boolean onGrieferGames,
      boolean displayed) {
    if (!displayed || !addonEnabled || !chatEnabled || !featureEnabled) {
      return false;
    }
    return !onlyOnGrieferGames || onGrieferGames;
  }
}
