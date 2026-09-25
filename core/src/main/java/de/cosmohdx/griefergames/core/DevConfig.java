package de.cosmohdx.griefergames.core;

import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.configuration.loader.Config;
import net.labymod.api.configuration.loader.property.ConfigProperty;

/**
 * Settings that only exist in the addon development client.
 * The field in {@link GrieferGamesConfig} carries {@code @SettingDevelopment}.
 */
public class DevConfig extends Config {

  @SwitchSetting
  private final ConfigProperty<Boolean> logPayloads = new ConfigProperty<>(false);

  public boolean logPayloads() {
    return Boolean.TRUE.equals(this.logPayloads.get());
  }
}
