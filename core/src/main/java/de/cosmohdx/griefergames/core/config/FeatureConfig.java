package de.cosmohdx.griefergames.core.config;

import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.configuration.loader.Config;
import net.labymod.api.configuration.loader.annotation.ShowSettingInParent;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.configuration.settings.annotation.SettingRequires;

/**
 * Sub-config whose {@code enabled} switch gates every other setting in the same config.
 *
 * <p>{@code @ParentSwitch} was deprecated in LabyMod 4.2.11. The settings screen now reads
 * {@link SettingRequires} on the config type: every member except the named switch is disabled
 * while that switch is off. {@link ShowSettingInParent} keeps the switch on the parent row,
 * which is what {@code @ParentSwitch} used to do.
 */
@SettingRequires(FeatureConfig.ENABLED)
public abstract class FeatureConfig extends Config {

  public static final String ENABLED = "enabled";

  @ShowSettingInParent
  @SwitchSetting
  private final ConfigProperty<Boolean> enabled = new ConfigProperty<>(true);

  protected FeatureConfig() {
  }

  protected FeatureConfig(boolean enabledByDefault) {
    if (!enabledByDefault) {
      this.enabled.set(false);
      this.enabled.updateDefaultValue(false);
    }
  }

  public ConfigProperty<Boolean> enabled() {
    return this.enabled;
  }

  public boolean isEnabled() {
    return this.enabled.get();
  }

  protected boolean isOn(ConfigProperty<Boolean> setting) {
    return this.isEnabled() && Boolean.TRUE.equals(setting.get());
  }
}
