package de.cosmohdx.griefergames.feature.itempreview;

import de.cosmohdx.griefergames.core.config.FeatureConfig;
import net.labymod.api.client.gui.screen.widget.widgets.input.SliderWidget.SliderSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.configuration.settings.annotation.SettingRequires;

public class ItemPreviewConfig extends FeatureConfig {

  @SwitchSetting
  @SettingRequires(ENABLED)
  private final ConfigProperty<Boolean> showMap = new ConfigProperty<>(true);

  @SliderSetting(min = TooltipPreviewPlacement.MIN_SIZE, max = TooltipPreviewPlacement.MAX_SIZE)
  @SettingRequires(ENABLED)
  private final ConfigProperty<Integer> mapSize = new ConfigProperty<>(TooltipPreviewPlacement.DEFAULT_SIZE);

  @SwitchSetting
  @SettingRequires(ENABLED)
  private final ConfigProperty<Boolean> showHead = new ConfigProperty<>(true);

  @SliderSetting(min = TooltipPreviewPlacement.MIN_SIZE, max = TooltipPreviewPlacement.MAX_SIZE)
  @SettingRequires(ENABLED)
  private final ConfigProperty<Integer> headSize = new ConfigProperty<>(TooltipPreviewPlacement.DEFAULT_SIZE);

  @SwitchSetting
  @SettingRequires(ENABLED)
  private final ConfigProperty<Boolean> enchantmentGlint = new ConfigProperty<>(true);

  public boolean showMap() {
    return this.isOn(this.showMap);
  }

  public int mapSize() {
    return TooltipPreviewPlacement.clampSize(this.mapSize.get());
  }

  public boolean showHead() {
    return this.isOn(this.showHead);
  }

  public int headSize() {
    return TooltipPreviewPlacement.clampSize(this.headSize.get());
  }

  public boolean enchantmentGlint() {
    return this.isOn(this.enchantmentGlint);
  }
}
