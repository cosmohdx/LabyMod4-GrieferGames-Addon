package de.cosmohdx.griefergames.feature.chat.search;

/**
 * Chat tab a recorded line was shown in.
 */
public enum ChatSearchTab {
  MAIN,
  SECOND;

  public static ChatSearchTab fromName(String name) {
    if (name == null) {
      return MAIN;
    }
    try {
      return ChatSearchTab.valueOf(name.trim().toUpperCase());
    } catch (IllegalArgumentException ignored) {
      return MAIN;
    }
  }
}
