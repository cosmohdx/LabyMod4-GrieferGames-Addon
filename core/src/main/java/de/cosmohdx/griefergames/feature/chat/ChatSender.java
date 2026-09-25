package de.cosmohdx.griefergames.feature.chat;

/**
 * Player shown at the start of a chat line, using the name the line already displays.
 */
public record ChatSender(String name, Kind kind) {

  public enum Kind {
    PUBLIC,
    PRIVATE,
    PLOT
  }
}
