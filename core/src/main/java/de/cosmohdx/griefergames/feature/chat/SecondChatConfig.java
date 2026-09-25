package de.cosmohdx.griefergames.feature.chat;

import de.cosmohdx.griefergames.core.config.FeatureConfig;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.TextFieldWidget.TextFieldSetting;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.configuration.settings.annotation.SettingSection;

public class SecondChatConfig extends FeatureConfig {

  @TextFieldSetting
  private final ConfigProperty<String> chatTabName = new ConfigProperty<>("2nd Chat");

  @SwitchSetting
  private final ConfigProperty<Boolean> create = new ConfigProperty<>(true);

  @SwitchSetting
  private final ConfigProperty<Boolean> manageFilters = new ConfigProperty<>(true);

  @SwitchSetting
  private final ConfigProperty<Boolean> chatIndicators = new ConfigProperty<>(false);

  @SettingSection("categories")
  private final SecondChatCategoriesConfig categories = new SecondChatCategoriesConfig();

  @SettingSection("realname")
  private final RealnameConfig realname = new RealnameConfig();

  public String chatTabName() {
    return this.chatTabName.get();
  }

  public boolean createTab() {
    return this.isOn(this.create);
  }

  public boolean manageFilters() {
    return this.isOn(this.manageFilters);
  }

  public boolean chatIndicators() {
    return this.isOn(this.chatIndicators);
  }

  public SecondChatCategoriesConfig categories() {
    return this.categories;
  }

  public RealnameConfig realname() {
    return this.realname;
  }
}
