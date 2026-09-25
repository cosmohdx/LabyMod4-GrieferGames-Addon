package de.cosmohdx.griefergames.feature.chat;

import java.util.regex.Pattern;

/**
 * Recognises the timestamp {@link ChatTime} places in front of a line, so a head can sit behind it
 * no matter which listener runs first.
 */
public final class ChatTimeMarkers {

  private ChatTimeMarkers() {
  }

  public static boolean isLeadingStamp(String childPlainText, String format) {
    if (childPlainText == null || format == null || format.isBlank()) {
      return false;
    }
    String plainFormat = format.replace('&', '\u00A7').replaceAll("(?i)\u00A7[0-9a-fk-or]", "");
    StringBuilder regex = new StringBuilder();
    for (int index = 0; index < plainFormat.length();) {
      if (plainFormat.startsWith("{h}", index)
          || plainFormat.startsWith("{m}", index)
          || plainFormat.startsWith("{s}", index)) {
        regex.append("\\d{2}");
        index += 3;
      } else {
        regex.append(Pattern.quote(plainFormat.substring(index, index + 1)));
        index++;
      }
    }
    return childPlainText.stripTrailing().matches(regex.toString());
  }

  /**
   * @param timeBeforeMessage chat time is enabled and drawn in front of the line
   * @param firstChildIsStamp the first child is already that timestamp
   * @return child index where the head belongs
   */
  public static int insertionIndex(boolean timeBeforeMessage, boolean firstChildIsStamp) {
    if (timeBeforeMessage && firstChildIsStamp) {
      return 1;
    }
    return 0;
  }
}
