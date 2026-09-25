package de.cosmohdx.griefergames.feature.chat;

import de.cosmohdx.griefergames.core.GrieferGamesConfig;
import de.cosmohdx.griefergames.core.config.FeatureConfig;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.TextFieldWidget.TextFieldSetting;
import net.labymod.api.configuration.loader.property.ConfigProperty;

public class ChatTimeConfig extends FeatureConfig {

  @SwitchSetting
  private final ConfigProperty<Boolean> chatTimeAfterMessage = new ConfigProperty<>(false);

  @TextFieldSetting
  private final ConfigProperty<String> chatTimeFormat = new ConfigProperty<>(GrieferGamesConfig.DEFAULT_CHATTIME_FORMAT);

  public ChatTimeConfig() {
    super(false);
  }

  public boolean afterMessage() {
    return this.isOn(this.chatTimeAfterMessage);
  }

  public String format() {
    return this.chatTimeFormat.get();
  }
}
