package de.cosmohdx.griefergames.feature.chat;

import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.configuration.loader.Config;
import net.labymod.api.configuration.loader.property.ConfigProperty;

public class ChatFiltersConfig extends Config {

  @SwitchSetting
  private final ConfigProperty<Boolean> hideVoteMessages = new ConfigProperty<>(false);

  @SwitchSetting
  private final ConfigProperty<Boolean> hideNewsMessages = new ConfigProperty<>(false);

  @SwitchSetting
  private final ConfigProperty<Boolean> hideBlankLines = new ConfigProperty<>(true);

  @SwitchSetting
  private final ConfigProperty<Boolean> hideSupremeBlankLines = new ConfigProperty<>(true);

  public boolean hideVoteMessages() {
    return this.hideVoteMessages.get();
  }

  public boolean hideNewsMessages() {
    return this.hideNewsMessages.get();
  }

  public boolean hideBlankLines() {
    return this.hideBlankLines.get();
  }

  public boolean hideSupremeBlankLines() {
    return this.hideSupremeBlankLines.get();
  }
}
