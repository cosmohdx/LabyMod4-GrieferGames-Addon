package de.cosmohdx.griefergames.feature.chat;

import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.configuration.loader.Config;
import net.labymod.api.configuration.loader.property.ConfigProperty;

public class ChatDisplayConfig extends Config {

  @SwitchSetting
  private final ConfigProperty<Boolean> showPrefixInDisplayName = new ConfigProperty<>(true);

  public boolean showPrefixInDisplayName() {
    return this.showPrefixInDisplayName.get();
  }
}
