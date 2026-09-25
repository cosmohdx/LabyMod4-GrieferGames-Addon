package de.cosmohdx.griefergames.feature.plotborder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class PlotGridTest {

  private static final PlotGridProfile LEGACY = PlotGridProfile.LEGACY_NATURE_EXTREME;

  @ParameterizedTest
  @CsvSource({
      "-85, -126, -84",
      "-84, -84, -42",
      "-43, -84, -42",
      "-42, -42, -1",
      "-41, -42, -1",
      "-2, -42, -1",
      "0, 0, 42",
      "1, 0, 42",
      "41, 0, 42",
      "42, 42, 84",
      "43, 42, 84",
      "84, 84, 126",
      "100000, 99960, 100002",
      "-100000, -100002, -99960"
  })
  void legacyCellFollowsThe42GridAndShrinksTheMinus42Cell(int coord, int min, int maxExclusive) {
    PlotRect rect = PlotGrid.rectAt(coord, coord, LEGACY);

    assertEquals(min, rect.minX());
    assertEquals(maxExclusive, rect.maxX());
    assertEquals(min, rect.minZ());
    assertEquals(maxExclusive, rect.maxZ());
    int width = maxExclusive - min;
    assertEquals(coord >= -42 && coord <= -2 ? 41 : 42, width);
  }

  @Test
  void blockJustBelowZeroIsNotOnAPlot() {
    assertNull(PlotGrid.rectAt(-1, -1, LEGACY));
    assertNull(PlotGrid.rectAt(-1, 10, LEGACY));
    assertNull(PlotGrid.rectAt(10, -1, LEGACY));
  }

  @Test
  void mixedAxesKeepTheirOwnCells() {
    PlotRect rect = PlotGrid.rectAt(-41, 43, LEGACY);

    assertEquals(-42, rect.minX());
    assertEquals(-1, rect.maxX());
    assertEquals(42, rect.minZ());
    assertEquals(84, rect.maxZ());
    assertEquals(41, rect.widthX());
    assertEquals(42, rect.widthZ());
  }

  @Test
  void cellsTileExceptForTheNarrowZeroRow() {
    int x = PlotGrid.rectAt(-200, 0, LEGACY).minX();
    while (x < 200) {
      PlotRect rect = PlotGrid.rectAt(x, 0, LEGACY);
      if (rect == null) {
        assertEquals(-1, x);
        x++;
        continue;
      }
      assertEquals(x, rect.minX());
      int width = rect.widthX();
      assertEquals(rect.minX() == -42 ? 41 : 42, width);
      PlotRect next = PlotGrid.rectAt(rect.maxX(), 0, LEGACY);
      if (rect.maxX() == -1) {
        assertNull(next);
      } else {
        assertEquals(rect.maxX(), next.minX());
      }
      x = rect.maxX();
    }
  }

  @Test
  void uniformProfileDoesNotLeaveAGapBelowZero() {
    PlotGridProfile uniform = new PlotGridProfile("uniform", 42, -42, 42, 0, 255);

    PlotRect rect = PlotGrid.rectAt(-1, -42, uniform);
    assertEquals(-42, rect.minX());
    assertEquals(0, rect.maxX());
    assertEquals(42, rect.widthX());
  }

  @Test
  void linesFollowThePlotEdgesAndTheHeightBand() {
    PlotRect rect = PlotGrid.rectAt(10, 10, LEGACY);
    List<Line3d> lines = PlotGrid.lines(rect, 100, 2, 8, LEGACY);

    assertTrue(lines.stream().anyMatch(line ->
        line.x1() == 0 && line.x2() == 0 && line.z1() == 0 && line.z2() == 0 && line.y1() < line.y2()));
    assertTrue(lines.stream().anyMatch(line ->
        line.x1() == 42 && line.x2() == 42 && line.z1() == 0 && line.z2() == 42 && line.y1() == line.y2()));
    assertTrue(lines.stream().allMatch(line ->
        line.y1() >= 92 && line.y1() <= 108 && line.y2() >= 92 && line.y2() <= 108));
    assertTrue(lines.stream().anyMatch(line -> line.y1() == 92 && line.y2() == 92 && line.z1() == 0 && line.z2() == 42));
    assertTrue(lines.stream().noneMatch(line -> line.y1() == 93 || line.y2() == 93));
  }

  @Test
  void narrowPlotStillClosesTheFarEdge() {
    PlotRect rect = PlotGrid.rectAt(-10, 5, LEGACY);
    List<Line3d> lines = PlotGrid.lines(rect, 64, 8, 8, LEGACY);

    assertEquals(-42, rect.minX());
    assertEquals(-1, rect.maxX());
    assertTrue(lines.stream().anyMatch(line -> line.x1() == -1 && line.x2() == -1 && line.y1() != line.y2()));
    assertTrue(lines.stream().anyMatch(line -> line.x1() == -42 && line.x2() == -1 && line.y1() == line.y2()));
  }
}
