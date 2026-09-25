package de.cosmohdx.griefergames.feature.plotborder;

/**
 * Servers on which the legacy plot grid is drawn.
 */
public final class PlotBorderWorlds {

  private PlotBorderWorlds() {
  }

  public static boolean supports(boolean legacyNetwork, String subServer) {
    if (!legacyNetwork || subServer == null) {
      return false;
    }
    return subServer.equalsIgnoreCase("nature") || subServer.equalsIgnoreCase("extreme");
  }
}
