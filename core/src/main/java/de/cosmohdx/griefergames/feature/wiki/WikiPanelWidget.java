package de.cosmohdx.griefergames.feature.wiki;

import net.labymod.api.client.gui.screen.ScreenContext;
import net.labymod.api.client.gui.screen.activity.Link;
import net.labymod.api.client.gui.screen.state.RoundedData;
import net.labymod.api.client.gui.screen.widget.AbstractWidget;
import net.labymod.api.client.gui.screen.widget.Widget;
import net.labymod.api.client.gui.lss.property.annotation.AutoWidget;
import net.labymod.api.client.gui.screen.widget.attributes.bounds.BoundsType;
import net.labymod.api.util.bounds.Rectangle;

/** Small styled container used for the catalog cards and modal. */
@AutoWidget
@Link("wiki.lss")
public final class WikiPanelWidget extends AbstractWidget<Widget> {
  private final int fill;
  private final int border;
  private final float radius;

  public WikiPanelWidget(int fill, int border, float radius) {
    this.fill = fill;
    this.border = border;
    this.radius = radius;
  }

  @Override
  public void renderWidget(ScreenContext context) {
    var bounds = this.bounds().rectangle(BoundsType.OUTER);
    // ScrollWidget translates ScreenContext's stack, while the canvas has a
    // separate pose. Move this panel by the same amount as its child widgets.
    var displacement = context.stack().transformVector(bounds);
    var rectangle = Rectangle.absolute(
        bounds.getLeft() + displacement.getX(),
        bounds.getTop() + displacement.getY(),
        bounds.getRight() + displacement.getZ(),
        bounds.getBottom() + displacement.getW());
    if (this.radius > 0 || (this.border >>> 24) != 0) {
      RoundedData rounded = RoundedData.builder().setBounds(rectangle).setRadius(this.radius)
          .setBorderThickness((this.border >>> 24) == 0 ? 0 : 1)
          .setBorderColor(this.border).build();
      context.canvas().submitRoundedRect(rectangle, this.fill, rounded);
    } else {
      context.canvas().submitRect(rectangle, this.fill);
    }
    // Keep the panel behind its image and text. Without a layer boundary the
    // canvas batches rectangles and can draw them over earlier children while
    // the scroll container moves.
    context.canvas().nextLayer();
    super.renderWidget(context);
  }
}
