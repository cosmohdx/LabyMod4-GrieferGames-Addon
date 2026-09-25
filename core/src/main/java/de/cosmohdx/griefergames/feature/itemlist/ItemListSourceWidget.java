package de.cosmohdx.griefergames.feature.itemlist;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import net.labymod.api.client.gui.lss.property.annotation.AutoWidget;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.widget.Widget;
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
 * Read-only source lines. No click, no copy.
 */
@AutoWidget
@SettingWidget
public class ItemListSourceWidget extends VerticalListWidget<Widget> {

  private static final int GRAY = 0xFFC6C6C6;

  public ItemListSourceWidget() {
  }

  @Override
  public void initialize(Parent parent) {
    super.initialize(parent);
    // reset() clears children before this widget is shown in the settings screen.
    this.addChild(this.line(ItemImages.itemsUrl()));
    this.addChild(this.line(ItemImages.categoriesUrl()));
  }

  private ComponentWidget line(String text) {
    ComponentWidget line = ComponentWidget.text(text);
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
  public static class Factory implements WidgetFactory<Display, ItemListSourceWidget> {

    @Override
    public Class<?>[] types() {
      return new Class[0];
    }

    @Override
    public ItemListSourceWidget[] create(Setting setting, Display annotation, SettingAccessor accessor) {
      return new ItemListSourceWidget[]{new ItemListSourceWidget()};
    }
  }
}
