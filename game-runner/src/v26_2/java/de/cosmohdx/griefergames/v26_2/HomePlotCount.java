package de.cosmohdx.griefergames.v26_2;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Reads the plot total shown by GrieferGames' /zuhause axe menu. */
public final class HomePlotCount {
  private static final Pattern PLOTS = Pattern.compile("^([1-9][0-9]{0,3})\\s+Grundstück(?:e)?$");
  private HomePlotCount() {}

  public static int fromLoreLine(String text) {
    if (text == null) return 0;
    Matcher match = PLOTS.matcher(text.strip());
    return match.matches() ? Integer.parseInt(match.group(1)) : 0;
  }
}
