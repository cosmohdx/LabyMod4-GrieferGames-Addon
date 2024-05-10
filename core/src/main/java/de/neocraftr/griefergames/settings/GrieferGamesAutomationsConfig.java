package de.neocraftr.griefergames.settings;

import de.neocraftr.griefergames.enums.ChatColor;
import net.labymod.api.client.gui.screen.widget.widgets.input.SliderWidget.SliderSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.TextFieldWidget.TextFieldSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.color.ColorPickerWidget.ColorPickerSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.dropdown.DropdownWidget.DropdownSetting;
import net.labymod.api.configuration.loader.Config;
import net.labymod.api.configuration.loader.annotation.ParentSwitch;
import net.labymod.api.configuration.loader.annotation.SpriteSlot;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.configuration.settings.annotation.SettingSection;
import java.awt.*;

public class GrieferGamesAutomationsConfig extends Config {

  //@ParentSwitch
  private final ConfigProperty<Boolean> enabled = new ConfigProperty<Boolean>(true);

  // 1.8
  @SpriteSlot(x = 0, y = 1)
  private GrieferGamesBoosterToolsConfig boosterConfig = new GrieferGamesBoosterToolsConfig();

  @SpriteSlot(x = 1, y = 1)
  private GrieferGamesAFKConfig afkConfig = new GrieferGamesAFKConfig();

  @SwitchSetting
  private final ConfigProperty<Boolean> autoPortal = new ConfigProperty<>(false);

  @SwitchSetting
  private final ConfigProperty<Boolean> sendSubServer = new ConfigProperty<>(false);

  @SettingSection("chatcolor")
  @DropdownSetting
  private final ConfigProperty<ChatColor> autoColor = new ConfigProperty<>(ChatColor.NONE);

  @SwitchSetting
  private final ConfigProperty<Boolean> autoColorCloud = new ConfigProperty<>(false);

  @ColorPickerSetting
  private final ConfigProperty<Integer> autoColorCloudColor = new ConfigProperty<>(new Color(255,255,255).getRGB());

  @SwitchSetting
  private final ConfigProperty<Boolean> colorGradiantCloud = new ConfigProperty<>(true);


  public GrieferGamesBoosterToolsConfig boosterConfig() {
    return boosterConfig;
  }

  public GrieferGamesAFKConfig afkConfig() {
    return afkConfig;
  }

  public boolean isAutoPortalEnabled() {
    return enabled.get() && autoPortal.get();
  }

  public ConfigProperty<Boolean> sendSubServer() {
    return sendSubServer;
  }

  public ConfigProperty<ChatColor> autoColor() {
    return autoColor;
  }

  public ConfigProperty<Boolean> autoColorCloud() {
    return autoColorCloud;
  }

  public ConfigProperty<Integer> autoColorCloudColor() {
    return autoColorCloudColor;
  }

  public ConfigProperty<Boolean> colorGradiantCloud() {
    return colorGradiantCloud;
  }
}
