package de.cosmohdx.griefergames.feature.chat;

import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.configuration.loader.Config;
import net.labymod.api.configuration.loader.property.ConfigProperty;

public class TeleportConfig extends Config {

  @SwitchSetting
  private final ConfigProperty<Boolean> highlightTPA = new ConfigProperty<>(true);

  public boolean highlightTpa() {
    return this.highlightTPA.get();
  }
}
