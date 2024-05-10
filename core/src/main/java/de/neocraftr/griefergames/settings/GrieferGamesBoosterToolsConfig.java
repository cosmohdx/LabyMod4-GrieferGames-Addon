package de.neocraftr.griefergames.settings;

import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.configuration.loader.Config;
import net.labymod.api.configuration.loader.property.ConfigProperty;

public class GrieferGamesBoosterToolsConfig extends Config {

  //@ParentSwitch
  @SwitchSetting
  private final ConfigProperty<Boolean> enabled = new ConfigProperty<Boolean>(true);

  @SwitchSetting
  private final ConfigProperty<Boolean> loadBoostersOnJoin = new ConfigProperty<>(true);

  @SwitchSetting
  private final ConfigProperty<Boolean> hideBoosterMenu = new ConfigProperty<>(false);

  public boolean isEnabled() {
    return enabled.get();
  }

  public boolean loadBoostersOnJoin() {
    return enabled.get() && loadBoostersOnJoin.get();
  }

  public boolean isHideBoosterMenu() {
    return enabled.get() && hideBoosterMenu.get();
  }

}
