package de.cosmohdx.griefergames.feature.plotborder;

import de.cosmohdx.griefergames.GrieferGames;
import de.cosmohdx.griefergames.core.HotkeyToggle;
import de.cosmohdx.griefergames.feature.subserver.GGNetworkTypeChangeEvent;
import de.cosmohdx.griefergames.feature.subserver.GGSubServerChangeEvent;
import java.util.List;
import net.labymod.api.Laby;
import net.labymod.api.client.entity.player.ClientPlayer;
import net.labymod.api.client.gui.screen.key.Key;
import net.labymod.api.event.Phase;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.input.KeyEvent;
import net.labymod.api.event.client.input.KeyEvent.State;
import net.labymod.api.event.client.render.world.RenderWorldEvent;
import net.labymod.api.util.I18n;

/**
 * Hotkey state for the plot grid. The grid is drawn only on the legacy network, on Nature
 * and Extreme. Leaving that network clears the hotkey; a sub-server change keeps it and
 * only drops the cached plot.
 */
public class PlotBorderListener {

  private final GrieferGames griefergames;
  private final HotkeyToggle hotkey = new HotkeyToggle();
  private PlotRect cachedRect;
  private int cachedBlockY = Integer.MIN_VALUE;
  private int cachedSpacing = Integer.MIN_VALUE;
  private int cachedHeightRange = Integer.MIN_VALUE;
  private List<Line3d> cachedLines = List.of();
  private boolean renderFailed;

  public PlotBorderListener(GrieferGames griefergames) {
    this.griefergames = griefergames;
  }

  @Subscribe
  public void onKey(KeyEvent event) {
    PlotBorderConfig config = this.config();
    if (!this.addonAllowsInput(config)) {
      return;
    }
    Key bound = config.key();
    if (bound.equals(Key.NONE) || !bound.equals(event.key()) || event.state() == State.HOLDING) {
      return;
    }
    if (event.state() == State.PRESS && Laby.labyAPI().minecraft().minecraftWindow().isScreenOpened()) {
      return;
    }
    boolean pressed = event.state() == State.PRESS;
    this.hotkey.apply(config.activation(), pressed, config.notifyToggle(), this::announce);
  }

  @Subscribe
  public void onNetworkTypeChange(GGNetworkTypeChangeEvent event) {
    this.hotkey.reset();
    this.clearCache();
  }

  @Subscribe
  public void onSubServerChange(GGSubServerChangeEvent event) {
    this.clearCache();
  }

  @Subscribe
  public void onRenderWorld(RenderWorldEvent event) {
    if (event.phase() != Phase.POST || this.renderFailed) {
      return;
    }
    PlotBorderConfig config = this.config();
    if (!this.shouldDraw(config)) {
      return;
    }
    if (Laby.labyAPI().minecraft().options().isHideGUI()) {
      return;
    }
    this.releaseHoldIfKeyIsUp(config);
    if (!this.hotkey.isActive()) {
      return;
    }
    ClientPlayer player = Laby.labyAPI().minecraft().getClientPlayer();
    if (player == null) {
      return;
    }
    if (event.stack() == null || event.camera() == null || event.camera().renderPosition() == null) {
      return;
    }
    int blockX = floorBlock(player.position().getX());
    int blockY = floorBlock(player.position().getY());
    int blockZ = floorBlock(player.position().getZ());
    try {
      List<Line3d> lines = this.linesFor(blockX, blockY, blockZ, config);
      PlotBorderRenderer.draw(event.stack(), event.camera().renderPosition(), lines, config.colorArgb());
    } catch (RuntimeException exception) {
      this.renderFailed = true;
      this.griefergames.logger().warn(GrieferGames.LOG_PREFIX + "Plot borders could not be drawn.", exception);
    }
  }

  private List<Line3d> linesFor(int blockX, int blockY, int blockZ, PlotBorderConfig config) {
    PlotGridProfile profile = PlotGridProfile.LEGACY_NATURE_EXTREME;
    if (this.cachedRect == null || !this.cachedRect.contains(blockX, blockZ)) {
      this.cachedRect = PlotGrid.rectAt(blockX, blockZ, profile);
      this.cachedLines = List.of();
      this.cachedSpacing = Integer.MIN_VALUE;
    }
    if (this.cachedRect == null) {
      return List.of();
    }
    int spacing = config.lineSpacing();
    int heightRange = config.heightRange();
    if (spacing != this.cachedSpacing
        || heightRange != this.cachedHeightRange
        || blockY != this.cachedBlockY
        || this.cachedLines.isEmpty()) {
      this.cachedLines = List.copyOf(PlotGrid.lines(this.cachedRect, blockY, spacing, heightRange, profile));
      this.cachedSpacing = spacing;
      this.cachedHeightRange = heightRange;
      this.cachedBlockY = blockY;
    }
    return this.cachedLines;
  }

  private void releaseHoldIfKeyIsUp(PlotBorderConfig config) {
    if (config.activation() != HotkeyToggle.Activation.HOLD || !this.hotkey.isActive()) {
      return;
    }
    Key bound = config.key();
    if (bound.equals(Key.NONE) || !Laby.labyAPI().minecraft().isKeyPressed(bound)) {
      this.hotkey.apply(HotkeyToggle.Activation.HOLD, false);
    }
  }

  private boolean shouldDraw(PlotBorderConfig config) {
    return this.addonAllowsInput(config)
        && this.hotkey.isActive()
        && this.supportedHere();
  }

  private boolean addonAllowsInput(PlotBorderConfig config) {
    return this.griefergames.configuration().enabled().get()
        && config.isEnabled()
        && this.griefergames.state().isOnGrieferGames();
  }

  private boolean supportedHere() {
    return PlotBorderWorlds.supports(
        this.griefergames.state().isLegacyNetwork(),
        this.griefergames.state().getSubServer());
  }

  private void announce(boolean active) {
    String key;
    if (!this.supportedHere()) {
      key = "plotBordersUnavailable";
    } else if (active) {
      key = "plotBordersOn";
    } else {
      key = "plotBordersOff";
    }
    this.griefergames.displayAddonMessage(I18n.translate(this.griefergames.namespace() + ".messages." + key));
  }

  private PlotBorderConfig config() {
    return this.griefergames.configuration().plotBorders();
  }

  private void clearCache() {
    this.cachedRect = null;
    this.cachedLines = List.of();
    this.cachedBlockY = Integer.MIN_VALUE;
    this.cachedSpacing = Integer.MIN_VALUE;
    this.cachedHeightRange = Integer.MIN_VALUE;
  }

  private static int floorBlock(double coord) {
    return (int) Math.floor(coord);
  }
}
