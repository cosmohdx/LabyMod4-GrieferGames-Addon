package de.cosmohdx.griefergames.feature.booster;

import de.cosmohdx.griefergames.core.config.FeatureConfig;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.configuration.loader.property.ConfigProperty;

public class GrieferGamesBoosterToolsConfig extends FeatureConfig {

  @SwitchSetting
  private final ConfigProperty<Boolean> loadBoostersOnJoin = new ConfigProperty<>(true);

  @SwitchSetting
  private final ConfigProperty<Boolean> hideBoosterMenu = new ConfigProperty<>(false);

  public boolean loadBoostersOnJoin() {
    return this.isOn(this.loadBoostersOnJoin);
  }

  public boolean hideBoosterMenu() {
    return this.isOn(this.hideBoosterMenu);
  }
}
