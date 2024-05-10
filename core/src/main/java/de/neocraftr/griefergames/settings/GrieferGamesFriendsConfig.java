package de.neocraftr.griefergames.settings;

import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.configuration.loader.Config;
import net.labymod.api.configuration.loader.annotation.ParentSwitch;
import net.labymod.api.configuration.loader.property.ConfigProperty;

public class GrieferGamesFriendsConfig extends Config {

  //@ParentSwitch
  private final ConfigProperty<Boolean> enabled = new ConfigProperty<Boolean>(true);

  @SwitchSetting
  private final ConfigProperty<Boolean> labyChatShowSubServerEnabled = new ConfigProperty<>(true);

  @SwitchSetting
  private final ConfigProperty<Boolean> discordShowSubServerEnabled = new ConfigProperty<>(true);


  public ConfigProperty<Boolean> labyChatShowSubServerEnabled() {
    return labyChatShowSubServerEnabled;
  }

  public ConfigProperty<Boolean> discordShowSubServerEnabled() {
    return discordShowSubServerEnabled;
  }
}
