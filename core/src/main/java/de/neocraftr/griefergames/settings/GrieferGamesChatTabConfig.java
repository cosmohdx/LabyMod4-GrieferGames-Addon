package de.neocraftr.griefergames.settings;

import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.TextFieldWidget.TextFieldSetting;
import net.labymod.api.configuration.loader.Config;
import net.labymod.api.configuration.loader.annotation.ParentSwitch;
import net.labymod.api.configuration.loader.property.ConfigProperty;

public class GrieferGamesChatTabConfig extends Config {

  //@ParentSwitch
  private final ConfigProperty<Boolean> enabled = new ConfigProperty<Boolean>(true);

  @TextFieldSetting
  private final ConfigProperty<String> chatTabName = new ConfigProperty<>("2nd Chat");

  @SwitchSetting
  private final ConfigProperty<Boolean> create = new ConfigProperty<Boolean>(true);

  @SwitchSetting
  private final ConfigProperty<Boolean> manageFilters = new ConfigProperty<Boolean>(true);

  @SwitchSetting
  private final ConfigProperty<Boolean> chatIndicators = new ConfigProperty<Boolean>(false);

  public boolean isEnabled() {
    return this.enabled.get();
  }

  public String getChatTabName() {
    return this.chatTabName.get();
  }

  public boolean isCreate() {
    return this.create.get();
  }

  public boolean isManageFilters() {
    return this.manageFilters.get();
  }

  public boolean isUseChatIndicators() {
    return this.chatIndicators.get();
  }

}
