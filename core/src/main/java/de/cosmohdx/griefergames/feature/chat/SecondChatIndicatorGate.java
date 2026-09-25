package de.cosmohdx.griefergames.feature.chat;

/**
 * Decides whether a line that reached the second chat tab stays visible when chat indicators are
 * on.
 *
 * <p>LabyMod only counts a line as unread when it passes {@code IngameChatTab#handleInput}. That
 * path also runs the tab filters. A custom tab with no matching filter hides the line, and a tab
 * with no filters at all hides it when any other tab's filter matches. Moved messages have to
 * survive that second case, while filters on the second chat itself still apply.
 */
public final class SecondChatIndicatorGate {

  public enum Action {
    LEAVE,
    SHOW,
    HIDE
  }

  private SecondChatIndicatorGate() {
  }

  public static Action decide(
      boolean secondChatTab,
      boolean indicatorsEnabled,
      boolean markedByAddon,
      boolean hasOwnFilters,
      boolean ownFilterMatches,
      boolean ownFilterHides) {
    if (!secondChatTab || !indicatorsEnabled) {
      return Action.LEAVE;
    }
    if (!markedByAddon || ownFilterHides || (hasOwnFilters && !ownFilterMatches)) {
      return Action.HIDE;
    }
    return Action.SHOW;
  }
}
