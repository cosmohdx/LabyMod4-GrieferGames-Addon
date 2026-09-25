package de.cosmohdx.griefergames.feature.plotborder;

/**
 * One plot on the horizontal grid. {@code maxX} and {@code maxZ} are exclusive.
 */
public record PlotRect(int minX, int maxX, int minZ, int maxZ) {

  public PlotRect {
    if (maxX <= minX || maxZ <= minZ) {
      throw new IllegalArgumentException("empty plot");
    }
  }

  public boolean contains(int blockX, int blockZ) {
    return blockX >= this.minX && blockX < this.maxX && blockZ >= this.minZ && blockZ < this.maxZ;
  }

  public int widthX() {
    return this.maxX - this.minX;
  }

  public int widthZ() {
    return this.maxZ - this.minZ;
  }
}
