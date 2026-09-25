package de.cosmohdx.griefergames.feature.chat;

/**
 * Shared pieces of the GrieferGames chat formats used by {@link SecondChatCategory}.
 */
final class SecondChatFormats {

  static final String RANK_NAME = "([A-Za-z\\-\\+]+) \\u2503 (~?\\!?\\w{1,16})";
  static final String MONEY = "(?:[1-9]\\d{0,2}(?:,\\d{1,3})*|0)(?:\\.\\d+)?";

  private SecondChatFormats() {
  }
}
