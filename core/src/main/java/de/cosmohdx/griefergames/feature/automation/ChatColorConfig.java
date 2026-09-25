package de.cosmohdx.griefergames.feature.automation;

import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.color.ColorPickerWidget.ColorPickerSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.dropdown.DropdownWidget.DropdownSetting;
import net.labymod.api.configuration.loader.Config;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import java.awt.Color;

public class ChatColorConfig extends Config {

  @DropdownSetting
  private final ConfigProperty<ChatColor> autoColor = new ConfigProperty<>(ChatColor.NONE);

  @SwitchSetting
  private final ConfigProperty<Boolean> autoColorCloud = new ConfigProperty<>(false);

  @ColorPickerSetting
  private final ConfigProperty<Integer> autoColorCloudColor = new ConfigProperty<>(new Color(255, 255, 255).getRGB());

  @SwitchSetting
  private final ConfigProperty<Boolean> colorGradientCloud = new ConfigProperty<>(true);

  public ChatColor autoColor() {
    return this.autoColor.get();
  }

  public boolean autoColorCloud() {
    return this.autoColorCloud.get();
  }

  public int autoColorCloudColor() {
    return this.autoColorCloudColor.get();
  }

  public boolean colorGradientCloud() {
    return this.colorGradientCloud.get();
  }
}
