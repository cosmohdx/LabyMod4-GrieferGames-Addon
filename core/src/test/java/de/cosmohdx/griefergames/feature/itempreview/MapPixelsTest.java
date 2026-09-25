package de.cosmohdx.griefergames.feature.itempreview;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class MapPixelsTest {

  @Test
  void returnsNullWhenTheMapIsNotLoaded() {
    assertNull(MapPixels.toArgb(null, packed -> 1));
    assertNull(MapPixels.toArgb(new byte[10], packed -> 1));
  }

  @Test
  void paintsExploredPixelsAndLeavesUnexploredCellsCheckerboard() {
    byte[] colors = new byte[MapPixels.AREA];
    colors[0] = 0;
    colors[1] = (byte) ((2 << 2) | 1);

    int[] argb = MapPixels.toArgb(colors, packed -> 0x112233);

    assertEquals(MapPixels.unexplored(0), argb[0]);
    assertEquals(0xFF112233, argb[1]);
  }

  @Test
  void keepsAnExistingAlphaChannel() {
    byte[] colors = new byte[MapPixels.AREA];
    colors[0] = 4;

    int[] argb = MapPixels.toArgb(colors, packed -> 0x80ABCDEF);

    assertEquals(0x80ABCDEF, argb[0]);
  }
}
