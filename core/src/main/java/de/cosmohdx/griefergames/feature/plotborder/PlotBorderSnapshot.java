package de.cosmohdx.griefergames.feature.plotborder;

import java.util.List;

record PlotBorderSnapshot(List<Line3d> lines, int colorArgb, double camX, double camY, double camZ) {
}
