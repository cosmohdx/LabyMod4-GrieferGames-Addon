package de.cosmohdx.griefergames.feature.chat.search;

/**
 * Filter for the search window: all tabs or one specific tab.
 */
public enum ChatSearchTabFilter {
  ALL,
  MAIN,
  SECOND;

  public boolean accepts(ChatSearchTab tab) {
    if (this == ALL || tab == null) {
      return true;
    }
    return this.name().equals(tab.name());
  }
}
