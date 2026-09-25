package de.cosmohdx.griefergames.feature.automation;

import de.cosmohdx.griefergames.core.config.FeatureConfig;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.configuration.loader.property.ConfigProperty;

public class GrieferGamesAutomationsConfig extends FeatureConfig {

  @SwitchSetting
  private final ConfigProperty<Boolean> autoPortal = new ConfigProperty<>(false);

  @SwitchSetting
  private final ConfigProperty<Boolean> sendSubServer = new ConfigProperty<>(false);

  private final ChatColorConfig chatColor = new ChatColorConfig();

  public ChatColorConfig chatColor() {
    return this.chatColor;
  }

  public boolean autoPortal() {
    return this.isOn(this.autoPortal);
  }

  public boolean announceSubServer() {
    return this.isOn(this.sendSubServer);
  }

  public ChatColor autoColor() {
    if (!this.isEnabled()) {
      return ChatColor.NONE;
    }
    return this.chatColor.autoColor();
  }

  public boolean autoColorCloud() {
    return this.isEnabled() && this.chatColor.autoColorCloud();
  }

  public int autoColorCloudColor() {
    return this.chatColor.autoColorCloudColor();
  }

  public boolean colorGradientCloud() {
    return this.isEnabled() && this.chatColor.colorGradientCloud();
  }
}
