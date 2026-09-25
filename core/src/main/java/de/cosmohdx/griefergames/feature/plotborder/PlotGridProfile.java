package de.cosmohdx.griefergames.feature.plotborder;

/**
 * Replaceable plot-grid measurements. The renderer only asks this profile for a rectangle,
 * so a later cloud layout can be added without changing the line drawing.
 *
 * <p>On the legacy Nature and Extreme worlds the cell size is 42. The cell whose origin is
 * {@code -42} is only 41 blocks wide, so one block directly below zero is not part of a plot.
 */
public record PlotGridProfile(
    String id,
    int size,
    int narrowOrigin,
    int narrowWidth,
    int minWorldY,
    int maxWorldY
) {

  public static final PlotGridProfile LEGACY_NATURE_EXTREME =
      new PlotGridProfile("legacy-nature-extreme", 42, -42, 41, 0, 255);

  public PlotGridProfile {
    if (id == null || id.isBlank()) {
      throw new IllegalArgumentException("id");
    }
    if (size < 2) {
      throw new IllegalArgumentException("size");
    }
    if (narrowWidth < 1 || narrowWidth > size) {
      throw new IllegalArgumentException("narrowWidth");
    }
    if (maxWorldY < minWorldY) {
      throw new IllegalArgumentException("world height");
    }
  }
}
