package de.neocraftr.griefergames.settings;

import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.configuration.loader.Config;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.configuration.settings.annotation.SettingSection;

public class GrieferGamesPaymentsConfig extends Config {
  @SwitchSetting
  private final ConfigProperty<Boolean> logTransactions = new ConfigProperty<>(false);

  @SettingSection("payments")

  @SwitchSetting
  private final ConfigProperty<Boolean> payChatRight = new ConfigProperty<>(true);

  @SwitchSetting
  private final ConfigProperty<Boolean> payAchievement = new ConfigProperty<>(true);

  @SwitchSetting
  private final ConfigProperty<Boolean> payHighlight = new ConfigProperty<>(true);

  @SwitchSetting
  private final ConfigProperty<Boolean> fakeMoneyWarning = new ConfigProperty<>(true);

  @SwitchSetting
  private final ConfigProperty<Boolean> showBalance = new ConfigProperty<>(false);

  @SettingSection("bank")

  @SwitchSetting
  private final ConfigProperty<Boolean> bankChatRight = new ConfigProperty<>(true);

  @SwitchSetting
  private final ConfigProperty<Boolean> bankAchievement = new ConfigProperty<>(false);

  @SwitchSetting
  private final ConfigProperty<Boolean> showBankBallance = new ConfigProperty<>(false);


  public ConfigProperty<Boolean> logTransactions() {
    return logTransactions;
  }

  public ConfigProperty<Boolean> payChatRight() {
    return payChatRight;
  }

  public ConfigProperty<Boolean> payAchievement() {
    return payAchievement;
  }

  public ConfigProperty<Boolean> payHighlight() {
    return payHighlight;
  }

  public ConfigProperty<Boolean> fakeMoneyWarning() {
    return fakeMoneyWarning;
  }

  public ConfigProperty<Boolean> showBalance() {
    return showBalance;
  }

  public ConfigProperty<Boolean> bankChatRight() {
    return bankChatRight;
  }

  public ConfigProperty<Boolean> bankAchievement() {
    return bankAchievement;
  }

  public ConfigProperty<Boolean> showBankBallance() {
    return showBankBallance;
  }
}
