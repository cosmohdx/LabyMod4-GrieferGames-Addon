package de.cosmohdx.griefergames.feature.chat;

import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.configuration.loader.Config;
import net.labymod.api.configuration.loader.property.ConfigProperty;

public class ChatCommandsConfig extends Config {

  @SwitchSetting
  private final ConfigProperty<Boolean> clickToReply = new ConfigProperty<>(true);

  @SwitchSetting
  private final ConfigProperty<Boolean> preventCommandFailure = new ConfigProperty<>(true);

  @SwitchSetting
  private final ConfigProperty<Boolean> correctCommandCapitalisation = new ConfigProperty<>(true);

  @SwitchSetting
  private final ConfigProperty<Boolean> betterIgnoreList = new ConfigProperty<>(true);

  public boolean clickToReply() {
    return this.clickToReply.get();
  }

  public boolean preventCommandFailure() {
    return this.preventCommandFailure.get();
  }

  public boolean correctCommandCapitalisation() {
    return this.correctCommandCapitalisation.get();
  }

  public boolean betterIgnoreList() {
    return this.betterIgnoreList.get();
  }
}
