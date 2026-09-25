package de.cosmohdx.griefergames.feature.item;

import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.configuration.loader.Config;
import net.labymod.api.configuration.loader.property.ConfigProperty;

public class ItemTooltipConfig extends Config {

  @SwitchSetting
  private final ConfigProperty<Boolean> mapTooltipPreview = new ConfigProperty<>(true);

  @SwitchSetting
  private final ConfigProperty<Boolean> headTooltipPreview = new ConfigProperty<>(true);

  public ConfigProperty<Boolean> mapTooltipPreview() {
    return this.mapTooltipPreview;
  }

  public ConfigProperty<Boolean> headTooltipPreview() {
    return this.headTooltipPreview;
  }
}
