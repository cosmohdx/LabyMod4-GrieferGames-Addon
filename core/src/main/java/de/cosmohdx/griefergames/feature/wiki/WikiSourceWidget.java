package de.cosmohdx.griefergames.feature.wiki;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import net.labymod.api.client.gui.lss.property.annotation.AutoWidget;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.widget.Widget;
import net.labymod.api.client.gui.screen.widget.size.SizeType;
import net.labymod.api.client.gui.screen.widget.size.WidgetSide;
import net.labymod.api.client.gui.screen.widget.size.WidgetSize;
import net.labymod.api.client.gui.screen.widget.widgets.ComponentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.list.VerticalListWidget;
import net.labymod.api.client.render.font.FontSize;
import net.labymod.api.client.render.font.FontSize.PredefinedFontSize;
import net.labymod.api.configuration.settings.Setting;
import net.labymod.api.configuration.settings.accessor.SettingAccessor;
import net.labymod.api.configuration.settings.annotation.SettingElement;
import net.labymod.api.configuration.settings.annotation.SettingFactory;
import net.labymod.api.configuration.settings.annotation.SettingWidget;
import net.labymod.api.configuration.settings.widget.WidgetFactory;

/**
 * Read-only source line. No click, no copy.
 */
@AutoWidget
@SettingWidget
public class WikiSourceWidget extends VerticalListWidget<Widget> {

  private static final int GRAY = 0xFFC6C6C6;

  public WikiSourceWidget() {
  }

  @Override
  public void initialize(Parent parent) {
    super.initialize(parent);
    // reset() clears children before this widget is shown in the settings screen.
    this.setSize(SizeType.ACTUAL, WidgetSide.WIDTH, WidgetSize.percentage(100));
    this.addChild(this.line(WikiSite.origin()));
  }

  private ComponentWidget line(String text) {
    ComponentWidget line = ComponentWidget.text(text);
    line.setSize(SizeType.ACTUAL, WidgetSide.WIDTH, WidgetSize.percentage(100));
    line.textColor().set(GRAY);
    line.fontSize().set(FontSize.predefined(PredefinedFontSize.SMALL));
    line.setPressable(() -> {
    });
    return line;
  }

  @SettingElement(extended = true)
  @Target(ElementType.METHOD)
  @Retention(RetentionPolicy.RUNTIME)
  public @interface Display {

  }

  @SettingFactory
  public static class Factory implements WidgetFactory<Display, WikiSourceWidget> {

    @Override
    public Class<?>[] types() {
      return new Class[0];
    }

    @Override
    public WikiSourceWidget[] create(Setting setting, Display annotation, SettingAccessor accessor) {
      return new WikiSourceWidget[]{new WikiSourceWidget()};
    }
  }
}
