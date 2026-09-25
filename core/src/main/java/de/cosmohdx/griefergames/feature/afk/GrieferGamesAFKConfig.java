package de.cosmohdx.griefergames.feature.afk;

import de.cosmohdx.griefergames.core.GrieferGamesConfig;
import de.cosmohdx.griefergames.core.config.FeatureConfig;
import net.labymod.api.client.gui.screen.widget.widgets.input.SliderWidget.SliderSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.TextFieldWidget.TextFieldSetting;
import net.labymod.api.configuration.loader.property.ConfigProperty;

public class GrieferGamesAFKConfig extends FeatureConfig {

  @SliderSetting(min = 1, max = 60)
  private final ConfigProperty<Integer> afkTime = new ConfigProperty<>(15);

  @SwitchSetting
  private final ConfigProperty<Boolean> afkNick = new ConfigProperty<>(false);

  @TextFieldSetting
  private final ConfigProperty<String> afkNickname = new ConfigProperty<>(GrieferGamesConfig.DEFAULT_AFK_NICKNAME);

  @SwitchSetting
  private final ConfigProperty<Boolean> afkMsgReply = new ConfigProperty<>(true);

  @TextFieldSetting
  private final ConfigProperty<String> afkMsgText = new ConfigProperty<>("Ich bin momentan AFK ;)");

  public int afkTimeMinutes() {
    return this.afkTime.get();
  }

  public boolean changeNickname() {
    return this.isOn(this.afkNick);
  }

  public String nickname() {
    return this.afkNickname.get();
  }

  public boolean replyToMessages() {
    return this.isOn(this.afkMsgReply);
  }

  public String replyText() {
    return this.afkMsgText.get();
  }
}
