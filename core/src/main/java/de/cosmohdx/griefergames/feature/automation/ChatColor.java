package de.cosmohdx.griefergames.feature.automation;

public enum ChatColor {
  NONE(""),
  AQUA("b"),
  BLUE("9"),
  GREEN("a"),
  RED("c"),
  YELLOW("e"),
  GOLD("6"),
  PURPLE("d"),
  GRAY("7"),
  DARK_GRAY("8"),
  DARK_BLUE("1"),
  DARK_GREEN("2"),
  DARK_AQUA("3"),
  DARK_RED("4"),
  DARK_PURPLE("5"),
  BLACK("0"),
  WHITE("f");

  private String colorCode;

  ChatColor(String colorCode) {
    this.colorCode = colorCode;
  }

  public String getColorCode() {
    return colorCode;
  }
}
