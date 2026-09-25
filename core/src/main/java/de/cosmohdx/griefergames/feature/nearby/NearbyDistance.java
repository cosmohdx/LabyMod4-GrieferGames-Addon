package de.cosmohdx.griefergames.feature.nearby;

/**
 * Rough distance steps. The exact block count, the direction and the coordinates stay hidden.
 */
public final class NearbyDistance {

  private NearbyDistance() {
  }

  public static String coarse(double distance) {
    if (distance < 5.0D) {
      return "<5 m";
    }
    if (distance < 10.0D) {
      return "<10 m";
    }
    if (distance < 20.0D) {
      return "<20 m";
    }
    return "<32 m";
  }
}
