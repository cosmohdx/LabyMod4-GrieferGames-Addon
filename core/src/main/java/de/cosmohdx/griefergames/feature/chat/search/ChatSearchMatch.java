package de.cosmohdx.griefergames.feature.chat.search;

/**
 * A match inside the plain text of one chat line. Offsets are UTF-16 indexes into that text.
 */
public record ChatSearchMatch(int start, int end) {

  public ChatSearchMatch {
    if (start < 0 || end < start) {
      throw new IllegalArgumentException("invalid match range " + start + ".." + end);
    }
  }

  public int length() {
    return this.end - this.start;
  }
}
