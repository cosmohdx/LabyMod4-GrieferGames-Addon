package de.cosmohdx.griefergames.feature.mobremover;

import de.cosmohdx.griefergames.core.config.FeatureConfig;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.configuration.loader.property.ConfigProperty;

public class MobRemoverConfig extends FeatureConfig {

  @SwitchSetting
  private final ConfigProperty<Boolean> lastTimeHover = new ConfigProperty<>(true);

  @SwitchSetting
  private final ConfigProperty<Boolean> notification = new ConfigProperty<>(true);

  public boolean lastTimeHover() {
    return this.isOn(this.lastTimeHover);
  }

  public boolean notification() {
    return this.isOn(this.notification);
  }
}
