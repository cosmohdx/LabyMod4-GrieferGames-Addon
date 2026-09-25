package de.cosmohdx.griefergames.feature.plotborder;

import de.cosmohdx.griefergames.core.HotkeyToggle;
import de.cosmohdx.griefergames.core.config.FeatureConfig;
import net.labymod.api.client.gui.screen.key.Key;
import net.labymod.api.client.gui.screen.widget.widgets.input.KeybindWidget.KeyBindSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.SliderWidget.SliderSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.color.ColorPickerWidget.ColorPickerSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.dropdown.DropdownWidget.DropdownSetting;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.util.Color;

public class PlotBorderConfig extends FeatureConfig {

  @KeyBindSetting
  private final ConfigProperty<Key> key = new ConfigProperty<>(Key.NONE);

  @DropdownSetting
  private final ConfigProperty<PlotBorderActivation> activation = new ConfigProperty<>(PlotBorderActivation.TOGGLE);

  @SwitchSetting
  private final ConfigProperty<Boolean> notify = new ConfigProperty<>(true);

  @ColorPickerSetting
  private final ConfigProperty<Color> color = new ConfigProperty<>(Color.YELLOW);

  @SliderSetting(min = 1, max = 8)
  private final ConfigProperty<Integer> lineSpacing = new ConfigProperty<>(2);

  @SliderSetting(min = 8, max = 64)
  private final ConfigProperty<Integer> heightRange = new ConfigProperty<>(32);

  public PlotBorderConfig() {
    super(false);
  }

  public Key key() {
    Key bound = this.key.get();
    return bound == null ? Key.NONE : bound;
  }

  public HotkeyToggle.Activation activation() {
    PlotBorderActivation selected = this.activation.get();
    if (selected == PlotBorderActivation.HOLD) {
      return HotkeyToggle.Activation.HOLD;
    }
    return HotkeyToggle.Activation.TOGGLE;
  }

  public boolean notifyToggle() {
    return this.isOn(this.notify);
  }

  public int colorArgb() {
    Color selected = this.color.get();
    int argb = selected == null ? Color.YELLOW.get() : selected.get();
    if ((argb >>> 24) == 0) {
      argb |= 0xFF000000;
    }
    return argb;
  }

  public int lineSpacing() {
    return clamp(this.lineSpacing.get(), 1, 8, 2);
  }

  public int heightRange() {
    return clamp(this.heightRange.get(), 8, 64, 32);
  }

  private static int clamp(Integer value, int min, int max, int fallback) {
    if (value == null) {
      return fallback;
    }
    return Math.max(min, Math.min(max, value));
  }
}
