package de.cosmohdx.griefergames.feature.friends;

import de.cosmohdx.griefergames.core.config.FeatureConfig;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.configuration.loader.property.ConfigProperty;

public class GrieferGamesFriendsConfig extends FeatureConfig {

  @SwitchSetting
  private final ConfigProperty<Boolean> showSubServerInLabyChat = new ConfigProperty<>(true);

  @SwitchSetting
  private final ConfigProperty<Boolean> showSubServerInDiscord = new ConfigProperty<>(true);

  public boolean showSubServerInLabyChat() {
    return this.isOn(this.showSubServerInLabyChat);
  }

  public boolean showSubServerInDiscord() {
    return this.isOn(this.showSubServerInDiscord);
  }
}
