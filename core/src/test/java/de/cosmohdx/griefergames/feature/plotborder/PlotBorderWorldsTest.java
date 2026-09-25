package de.cosmohdx.griefergames.feature.plotborder;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PlotBorderWorldsTest {

  @Test
  void onlyLegacyNatureAndExtremeAreSupported() {
    assertTrue(PlotBorderWorlds.supports(true, "nature"));
    assertTrue(PlotBorderWorlds.supports(true, "Extreme"));
    assertFalse(PlotBorderWorlds.supports(true, "lava"));
    assertFalse(PlotBorderWorlds.supports(true, "wasser"));
    assertFalse(PlotBorderWorlds.supports(true, "event"));
    assertFalse(PlotBorderWorlds.supports(true, "cb12"));
    assertFalse(PlotBorderWorlds.supports(false, "nature"));
    assertFalse(PlotBorderWorlds.supports(true, null));
    assertFalse(PlotBorderWorlds.supports(true, ""));
  }
}
