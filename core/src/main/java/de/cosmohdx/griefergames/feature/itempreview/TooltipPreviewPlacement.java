package de.cosmohdx.griefergames.feature.itempreview;

/**
 * Places a square preview beside the cursor, preferring the side opposite the vanilla tooltip.
 */
public final class TooltipPreviewPlacement {

  public static final int MIN_SIZE = 64;
  public static final int MAX_SIZE = 128;
  public static final int DEFAULT_SIZE = 96;
  public static final int GAP = 10;

  private TooltipPreviewPlacement() {
  }

  public static int clampSize(int size) {
    if (size < MIN_SIZE) {
      return MIN_SIZE;
    }
    if (size > MAX_SIZE) {
      return MAX_SIZE;
    }
    return size;
  }

  public static int x(int mouseX, int size, int screenWidth) {
    int clamped = clampSize(size);
    int left = mouseX - GAP - clamped;
    if (left >= 0 && left + clamped <= screenWidth) {
      return left;
    }
    int right = mouseX + GAP;
    if (right >= 0 && right + clamped <= screenWidth) {
      return right;
    }
    if (screenWidth <= clamped) {
      return 0;
    }
    return screenWidth - clamped;
  }

  public static int y(int mouseY, int size, int screenHeight) {
    int clamped = clampSize(size);
    int top = mouseY - 12;
    if (top + clamped > screenHeight) {
      top = screenHeight - clamped;
    }
    if (top < 0) {
      top = 0;
    }
    return top;
  }
}
