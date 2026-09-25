package de.cosmohdx.griefergames.feature.chat;

import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.configuration.loader.Config;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.configuration.settings.annotation.SettingRequires;

public class ChatDisplayConfig extends Config {

  @SwitchSetting
  private final ConfigProperty<Boolean> showPrefixInDisplayName = new ConfigProperty<>(true);

  @SwitchSetting
  private final ConfigProperty<Boolean> showMessageHeads = new ConfigProperty<>(true);

  @SettingRequires("showMessageHeads")
  @SwitchSetting
  private final ConfigProperty<Boolean> messageHeadsGlobal = new ConfigProperty<>(true);

  @SettingRequires("showMessageHeads")
  @SwitchSetting
  private final ConfigProperty<Boolean> messageHeadsPrivate = new ConfigProperty<>(true);

  @SettingRequires("showMessageHeads")
  @SwitchSetting
  private final ConfigProperty<Boolean> messageHeadsPlot = new ConfigProperty<>(true);

  public boolean showPrefixInDisplayName() {
    return this.showPrefixInDisplayName.get();
  }

  public boolean showMessageHeads() {
    return this.showMessageHeads.get();
  }

  public boolean messageHeadsGlobal() {
    return this.messageHeadsGlobal.get();
  }

  public boolean messageHeadsPrivate() {
    return this.messageHeadsPrivate.get();
  }

  public boolean messageHeadsPlot() {
    return this.messageHeadsPlot.get();
  }
}
