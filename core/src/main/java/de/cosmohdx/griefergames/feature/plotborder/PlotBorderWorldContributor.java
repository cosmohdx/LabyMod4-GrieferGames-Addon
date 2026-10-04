package de.cosmohdx.griefergames.feature.plotborder;

import net.labymod.api.client.render.matrix.Stack;
import net.labymod.api.client.render.state.world.WorldSnapshotContext;
import net.labymod.api.client.render.state.world.WorldSnapshotContributor;
import net.labymod.api.laby3d.render.queue.SubmissionCollector;
import net.labymod.api.laby3d.renderer.snapshot.ExtraKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PlotBorderWorldContributor implements WorldSnapshotContributor<PlotBorderSnapshot> {

  static final ExtraKey<PlotBorderSnapshot> KEY = ExtraKey.of("plot_borders", PlotBorderSnapshot.class);

  private final PlotBorderListener listener;

  public PlotBorderWorldContributor(PlotBorderListener listener) {
    this.listener = listener;
  }

  @Override
  public @NotNull ExtraKey<PlotBorderSnapshot> key() {
    return KEY;
  }

  @Override
  public @Nullable PlotBorderSnapshot prepare(@NotNull WorldSnapshotContext context) {
    return this.listener.prepareSnapshot(context);
  }

  @Override
  public void submit(
      @NotNull Stack stack,
      @NotNull SubmissionCollector collector,
      @NotNull PlotBorderSnapshot data
  ) {
    PlotBorderRenderer.submit(collector, stack, data);
  }
}
