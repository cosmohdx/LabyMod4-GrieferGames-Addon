package de.cosmohdx.griefergames.feature.itempreview;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TooltipPreviewPlacementTest {

  @Test
  void clampsSizeToTheSliderRange() {
    assertEquals(64, TooltipPreviewPlacement.clampSize(10));
    assertEquals(96, TooltipPreviewPlacement.clampSize(96));
    assertEquals(128, TooltipPreviewPlacement.clampSize(400));
  }

  @Test
  void prefersTheSideOppositeTheVanillaTooltip() {
    assertEquals(50, TooltipPreviewPlacement.x(156, 96, 400));
  }

  @Test
  void flipsToTheRightWhenTheLeftSideDoesNotFit() {
    assertEquals(20, TooltipPreviewPlacement.x(10, 96, 400));
  }

  @Test
  void keepsThePreviewInsideTheScreen() {
    assertEquals(172, TooltipPreviewPlacement.x(390, 128, 300));
    assertEquals(0, TooltipPreviewPlacement.y(4, 96, 80));
    assertEquals(104, TooltipPreviewPlacement.y(200, 96, 200));
  }
}
