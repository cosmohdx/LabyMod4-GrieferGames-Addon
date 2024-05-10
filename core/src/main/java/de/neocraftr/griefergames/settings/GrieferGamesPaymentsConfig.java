package de.neocraftr.griefergames.settings;

import de.neocraftr.griefergames.GrieferGames;
import net.labymod.api.client.gui.screen.widget.widgets.input.ButtonWidget.ButtonSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.configuration.loader.Config;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.configuration.settings.Setting;
import net.labymod.api.configuration.settings.annotation.SettingSection;
import net.labymod.api.util.MethodOrder;

public class GrieferGamesPaymentsConfig extends Config {

  //@ParentSwitch
  @SwitchSetting
  private final ConfigProperty<Boolean> enabled = new ConfigProperty<Boolean>(true);

  @SwitchSetting
  private final ConfigProperty<Boolean> logTransactions = new ConfigProperty<>(false);

  @MethodOrder(after = "logTransactions")
  @ButtonSetting()
  public void openTransactionsFile(Setting setting) {
    GrieferGames.get().fileManager().openTransactionsFile();
  }

  @SettingSection("payments")

  @SwitchSetting
  private final ConfigProperty<Boolean> payChatRight = new ConfigProperty<>(true);

  @SwitchSetting
  private final ConfigProperty<Boolean> payAchievement = new ConfigProperty<>(true);

  @SwitchSetting
  private final ConfigProperty<Boolean> payHighlight = new ConfigProperty<>(true);

  @SwitchSetting
  private final ConfigProperty<Boolean> fakeMoneyWarning = new ConfigProperty<>(true);

  @SettingSection("bank")

  @SwitchSetting
  private final ConfigProperty<Boolean> bankChatRight = new ConfigProperty<>(true);

  @SwitchSetting
  private final ConfigProperty<Boolean> bankAchievement = new ConfigProperty<>(false);


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

  public ConfigProperty<Boolean> bankChatRight() {
    return bankChatRight;
  }

  public ConfigProperty<Boolean> bankAchievement() {
    return bankAchievement;
  }
}
