package de.cosmohdx.griefergames.feature.plotborder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.Nullable;

/**
 * Pure plot-grid math for a {@link PlotGridProfile}.
 *
 * <p>The origin of a cell is {@code floorDiv(coord, size) * size}. The cell whose origin
 * equals {@link PlotGridProfile#narrowOrigin()} is only {@link PlotGridProfile#narrowWidth()}
 * blocks wide. On the legacy profile that is the cell starting at {@code -42}, which ends at
 * {@code -1}. The leftover block at {@code -1} is the narrow zero row and belongs to no plot.
 */
public final class PlotGrid {

  private PlotGrid() {
  }

  @Nullable
  public static PlotRect rectAt(int blockX, int blockZ, PlotGridProfile profile) {
    Optional<Span> x = span(blockX, profile);
    Optional<Span> z = span(blockZ, profile);
    if (x.isEmpty() || z.isEmpty()) {
      return null;
    }
    return new PlotRect(x.get().min(), x.get().maxExclusive(), z.get().min(), z.get().maxExclusive());
  }

  /**
   * Vertical lines on the four edges, spaced by {@code spacing}, and a horizontal ring at the
   * same spacing inside the height band around the player.
   */
  public static List<Line3d> lines(
      PlotRect rect,
      int playerBlockY,
      int spacing,
      int heightRange,
      PlotGridProfile profile
  ) {
    int step = Math.max(1, spacing);
    int lower = clamp(playerBlockY - Math.max(0, heightRange), profile.minWorldY(), profile.maxWorldY());
    int upper = clamp(playerBlockY + Math.max(0, heightRange), profile.minWorldY(), profile.maxWorldY());
    if (lower > upper) {
      int swap = lower;
      lower = upper;
      upper = swap;
    }
    int yStart = alignUp(lower, step);
    int yEnd = alignDown(upper, step);
    if (yStart > yEnd) {
      int y = clamp(playerBlockY, profile.minWorldY(), profile.maxWorldY());
      yStart = y;
      yEnd = y;
    }

    List<Line3d> lines = new ArrayList<>();
    if (yEnd > yStart) {
      addVerticalEdges(lines, rect, yStart, yEnd, step);
    }
    for (int y = yStart; y <= yEnd; y += step) {
      addHorizontalRing(lines, rect, y);
    }
    return lines;
  }

  static Optional<Span> span(int coord, PlotGridProfile profile) {
    int size = profile.size();
    int origin = Math.floorDiv(coord, size) * size;
    int end = origin + size;
    if (origin == profile.narrowOrigin()) {
      end = origin + profile.narrowWidth();
    }
    if (coord < origin || coord >= end) {
      return Optional.empty();
    }
    return Optional.of(new Span(origin, end));
  }

  private static void addVerticalEdges(List<Line3d> lines, PlotRect rect, int yStart, int yEnd, int step) {
    for (int x = rect.minX(); x <= rect.maxX(); x += step) {
      lines.add(new Line3d(x, yStart, rect.minZ(), x, yEnd, rect.minZ()));
      lines.add(new Line3d(x, yStart, rect.maxZ(), x, yEnd, rect.maxZ()));
    }
    if (Math.floorMod(rect.maxX() - rect.minX(), step) != 0) {
      lines.add(new Line3d(rect.maxX(), yStart, rect.minZ(), rect.maxX(), yEnd, rect.minZ()));
      lines.add(new Line3d(rect.maxX(), yStart, rect.maxZ(), rect.maxX(), yEnd, rect.maxZ()));
    }
    for (int z = rect.minZ() + step; z < rect.maxZ(); z += step) {
      lines.add(new Line3d(rect.minX(), yStart, z, rect.minX(), yEnd, z));
      lines.add(new Line3d(rect.maxX(), yStart, z, rect.maxX(), yEnd, z));
    }
  }

  private static void addHorizontalRing(List<Line3d> lines, PlotRect rect, int y) {
    lines.add(new Line3d(rect.minX(), y, rect.minZ(), rect.maxX(), y, rect.minZ()));
    lines.add(new Line3d(rect.minX(), y, rect.maxZ(), rect.maxX(), y, rect.maxZ()));
    lines.add(new Line3d(rect.minX(), y, rect.minZ(), rect.minX(), y, rect.maxZ()));
    lines.add(new Line3d(rect.maxX(), y, rect.minZ(), rect.maxX(), y, rect.maxZ()));
  }

  private static int alignDown(int value, int step) {
    return value - Math.floorMod(value, step);
  }

  private static int alignUp(int value, int step) {
    int remainder = Math.floorMod(value, step);
    return remainder == 0 ? value : value + (step - remainder);
  }

  private static int clamp(int value, int min, int max) {
    return Math.max(min, Math.min(max, value));
  }

  record Span(int min, int maxExclusive) {
  }
}
