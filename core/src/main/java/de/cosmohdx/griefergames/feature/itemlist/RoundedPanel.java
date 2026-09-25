package de.cosmohdx.griefergames.feature.itemlist;

import net.labymod.api.client.gui.screen.ScreenContext;
import net.labymod.api.client.gui.screen.state.RoundedData;
import net.labymod.api.client.gui.screen.widget.SimpleWidget;
import net.labymod.api.client.gui.screen.widget.attributes.bounds.BoundsType;
import net.labymod.api.util.bounds.ReasonableMutableRectangle;
import net.labymod.api.util.bounds.Rectangle;
import net.labymod.api.util.math.vector.FloatVector4;

public class RoundedPanel extends SimpleWidget {

  private final int fill;
  private final int border;
  private final float radius;

  public RoundedPanel(int fill, int border, float radius) {
    this.fill = fill;
    this.border = border;
    this.radius = radius;
  }

  @Override
  public void renderWidget(ScreenContext context) {
    ReasonableMutableRectangle bounds = this.bounds().rectangle(BoundsType.OUTER);
    FloatVector4 transformed = context.stack().transformVector(bounds);
    Rectangle rectangle = Rectangle.absolute(
        bounds.getLeft() + transformed.getX(),
        bounds.getTop() + transformed.getY(),
        bounds.getRight() + transformed.getZ(),
        bounds.getBottom() + transformed.getW()
    );
    if (this.radius <= 0 && (this.border >>> 24) == 0) {
      context.canvas().submitRect(rectangle, this.fill);
    } else {
      float thickness = (this.border >>> 24) == 0 ? 0 : 1;
      RoundedData rounded = RoundedData.builder()
          .setBounds(rectangle)
          .setRadius(this.radius)
          .setBorderThickness(thickness)
          .setBorderColor(this.border)
          .build();
      context.canvas().submitRoundedRect(rectangle, this.fill, rounded);
    }
    context.canvas().nextLayer();
    super.renderWidget(context);
  }
}
