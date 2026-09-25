package de.cosmohdx.griefergames.feature.item;

import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.configuration.loader.Config;
import net.labymod.api.configuration.loader.property.ConfigProperty;

public class VisualsConfig extends Config {

  @SwitchSetting
  private final ConfigProperty<Boolean> headEnchantmentGlint = new ConfigProperty<>(true);

  @SwitchSetting
  private final ConfigProperty<Boolean> overstackingFix = new ConfigProperty<>(true);

  public ConfigProperty<Boolean> headEnchantmentGlint() {
    return this.headEnchantmentGlint;
  }

  public ConfigProperty<Boolean> overstackingFix() {
    return this.overstackingFix;
  }
}
