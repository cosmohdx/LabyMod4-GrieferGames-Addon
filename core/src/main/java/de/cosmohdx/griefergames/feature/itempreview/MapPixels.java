package de.cosmohdx.griefergames.feature.itempreview;

import java.util.function.IntUnaryOperator;

public final class MapPixels {

  public static final int SIZE = 128;
  public static final int AREA = SIZE * SIZE;

  private MapPixels() {
  }

  /**
   * Converts packed map colors. {@code packedColor} is only called for explored pixels.
   * A missing or short color array means the map is not loaded.
   */
  public static int[] toArgb(byte[] colors, IntUnaryOperator packedColor) {
    if (colors == null || colors.length < AREA || packedColor == null) {
      return null;
    }
    int[] argb = new int[AREA];
    for (int index = 0; index < AREA; index++) {
      int packed = colors[index] & 0xFF;
      if ((packed >> 2) == 0) {
        argb[index] = unexplored(index);
        continue;
      }
      int color = packedColor.applyAsInt(packed);
      argb[index] = withOpaqueAlpha(color);
    }
    return argb;
  }

  public static int unexplored(int index) {
    return ((index + index / SIZE & 1) * 8 + 16) << 24;
  }

  public static int withOpaqueAlpha(int color) {
    if ((color >>> 24) == 0) {
      return color | 0xFF000000;
    }
    return color;
  }
}
