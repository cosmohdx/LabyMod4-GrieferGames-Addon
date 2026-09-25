package de.cosmohdx.griefergames.feature.chat;

import de.cosmohdx.griefergames.core.config.FeatureConfig;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.TextFieldWidget.TextFieldSetting;
import net.labymod.api.configuration.loader.annotation.Exclude;
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

  /**
   * LabyMod persists a chat tab under {@code RootChatTabConfig#getUniqueID()}. The display name is
   * not that identity: it can be renamed, and an empty name falls back to the current server name.
   */
  @Exclude
  private final ConfigProperty<String> tabId = new ConfigProperty<>("");

  @SettingSection("categories")
  private final SecondChatCategoriesConfig categories = new SecondChatCategoriesConfig();

  @SettingSection("realname")
  private final RealnameConfig realname = new RealnameConfig();

  public String chatTabName() {
    return this.chatTabName.get();
  }

  public ConfigProperty<String> chatTabNameProperty() {
    return this.chatTabName;
  }

  public boolean createTab() {
    return this.isOn(this.create);
  }

  public ConfigProperty<Boolean> createProperty() {
    return this.create;
  }

  public boolean manageFilters() {
    return this.isOn(this.manageFilters);
  }

  public ConfigProperty<Boolean> manageFiltersProperty() {
    return this.manageFilters;
  }

  public boolean chatIndicators() {
    return this.isOn(this.chatIndicators);
  }

  public ConfigProperty<Boolean> chatIndicatorsProperty() {
    return this.chatIndicators;
  }

  public String tabId() {
    String id = this.tabId.get();
    return id == null ? "" : id;
  }

  public void tabId(String tabId) {
    this.tabId.set(tabId == null ? "" : tabId);
  }

  public SecondChatCategoriesConfig categories() {
    return this.categories;
  }

  public RealnameConfig realname() {
    return this.realname;
  }
}
