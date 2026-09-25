package de.cosmohdx.griefergames.feature.chat;

import net.labymod.api.client.gui.screen.widget.widgets.input.SliderWidget.SliderSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.configuration.loader.Config;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.configuration.settings.annotation.SettingRequires;

public class ChatCommandsConfig extends Config {

  @SwitchSetting
  private final ConfigProperty<Boolean> clickToReply = new ConfigProperty<>(true);

  @SwitchSetting
  private final ConfigProperty<Boolean> preventCommandFailure = new ConfigProperty<>(true);

  @SwitchSetting
  private final ConfigProperty<Boolean> correctCommandCapitalisation = new ConfigProperty<>(true);

  @SwitchSetting
  private final ConfigProperty<Boolean> betterIgnoreList = new ConfigProperty<>(true);

  @SwitchSetting
  private final ConfigProperty<Boolean> splitLongMessages = new ConfigProperty<>(false);

  @SettingRequires("splitLongMessages")
  @SliderSetting(min = 2, max = 4)
  private final ConfigProperty<Integer> splitMaxParts = new ConfigProperty<>(3);

  @SettingRequires("splitLongMessages")
  @SliderSetting(min = 2.5F, max = 10.0F, steps = 0.5F)
  private final ConfigProperty<Float> splitDelaySeconds = new ConfigProperty<>(3.0F);

  @SettingRequires("splitLongMessages")
  @SwitchSetting
  private final ConfigProperty<Boolean> splitConfirm = new ConfigProperty<>(false);

  @SettingRequires("splitLongMessages")
  @SwitchSetting
  private final ConfigProperty<Boolean> splitPrivateMessages = new ConfigProperty<>(true);

  public boolean clickToReply() {
    return this.clickToReply.get();
  }

  public boolean preventCommandFailure() {
    return this.preventCommandFailure.get();
  }

  public boolean correctCommandCapitalisation() {
    return this.correctCommandCapitalisation.get();
  }

  public boolean betterIgnoreList() {
    return this.betterIgnoreList.get();
  }

  public boolean splitLongMessages() {
    return this.splitLongMessages.get();
  }

  public int splitMaxParts() {
    Integer value = this.splitMaxParts.get();
    return value == null ? 3 : value;
  }

  public float splitDelaySeconds() {
    Float value = this.splitDelaySeconds.get();
    return value == null ? 3.0F : value;
  }

  public boolean splitConfirm() {
    return this.splitConfirm.get();
  }

  public boolean splitPrivateMessages() {
    return this.splitPrivateMessages.get();
  }
}
