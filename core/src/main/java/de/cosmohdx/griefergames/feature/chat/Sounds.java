package de.cosmohdx.griefergames.feature.chat;

import net.labymod.api.Laby;

public enum Sounds {
  NONE(""),
  BASS("bass"),
  BASSDRUM("bd"),
  HARP("harp"),
  HAT("hat"),
  PLING("pling"),
  SNARE("snare"),
  POP("pop");

  private String path;

  Sounds(String path) {
    this.path = path;
  }

  public String path() {
    if(path.isEmpty()) return path;

    return getVersionedPrefix()+path;
  }

  public String getVersionedPrefix() {
    return switch (Laby.labyAPI().minecraft().getVersion()) {
      case "1.8.9" -> "note.";
      case "1.12.2" -> "block.note.";
      default -> "block.note_block.";
    };
  }
}
