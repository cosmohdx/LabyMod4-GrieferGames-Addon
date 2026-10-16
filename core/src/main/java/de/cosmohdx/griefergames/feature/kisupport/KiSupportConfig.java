package de.cosmohdx.griefergames.feature.kisupport;

import de.cosmohdx.griefergames.core.config.FeatureConfig;
import de.cosmohdx.griefergames.feature.automation.ChatColor;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.dropdown.DropdownWidget.DropdownSetting;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.configuration.settings.annotation.SettingRequires;

public class KiSupportConfig extends FeatureConfig {

  @SwitchSetting
  private final ConfigProperty<Boolean> alfredCompletion = new ConfigProperty<>(true);

  @SwitchSetting
  private final ConfigProperty<Boolean> alfredSubtle = new ConfigProperty<>(true);

  @SettingRequires("alfredSubtle")
  @DropdownSetting
  private final ConfigProperty<ChatColor> alfredColor = new ConfigProperty<>(ChatColor.GREEN);

  @SwitchSetting
  private final ConfigProperty<Boolean> hideAlfred = new ConfigProperty<>(false);

  public ConfigProperty<Boolean> alfredCompletionProperty() {
    return this.alfredCompletion;
  }

  public ConfigProperty<Boolean> alfredSubtleProperty() {
    return this.alfredSubtle;
  }

  public ConfigProperty<ChatColor> alfredColorProperty() {
    return this.alfredColor;
  }

  public ConfigProperty<Boolean> hideAlfredProperty() {
    return this.hideAlfred;
  }

  public boolean alfredCompletion() {
    return this.isOn(this.alfredCompletion);
  }

  public boolean alfredSubtle() {
    return this.isOn(this.alfredSubtle);
  }

  public ChatColor alfredColor() {
    ChatColor color = this.alfredColor.get();
    return color == null ? ChatColor.GREEN : color;
  }

  public boolean hideAlfred() {
    return this.isOn(this.hideAlfred);
  }
}
