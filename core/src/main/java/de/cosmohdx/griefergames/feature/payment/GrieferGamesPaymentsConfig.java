package de.cosmohdx.griefergames.feature.payment;

import de.cosmohdx.griefergames.GrieferGames;
import de.cosmohdx.griefergames.core.config.FeatureConfig;
import net.labymod.api.client.gui.screen.widget.widgets.input.ButtonWidget.ButtonSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.configuration.settings.Setting;
import net.labymod.api.configuration.settings.annotation.SettingSection;
import net.labymod.api.util.MethodOrder;

public class GrieferGamesPaymentsConfig extends FeatureConfig {

  @SwitchSetting
  private final ConfigProperty<Boolean> logTransactions = new ConfigProperty<>(false);

  @MethodOrder(after = "logTransactions")
  @ButtonSetting
  public void openTransactionsFile(Setting setting) {
    GrieferGames.get().fileManager().openTransactionsFile();
  }

  @SettingSection("payments")
  @SwitchSetting
  private final ConfigProperty<Boolean> payAchievement = new ConfigProperty<>(true);

  @SwitchSetting
  private final ConfigProperty<Boolean> payHighlight = new ConfigProperty<>(true);

  @SwitchSetting
  private final ConfigProperty<Boolean> fakeMoneyWarning = new ConfigProperty<>(true);

  @SettingSection("bank")
  @SwitchSetting
  private final ConfigProperty<Boolean> bankAchievement = new ConfigProperty<>(false);

  public boolean logTransactions() {
    return this.isOn(this.logTransactions);
  }

  public boolean paymentNotification() {
    return this.isOn(this.payAchievement);
  }

  public boolean highlightPayments() {
    return this.isOn(this.payHighlight);
  }

  public boolean fakeMoneyWarning() {
    return this.isOn(this.fakeMoneyWarning);
  }

  public boolean bankNotification() {
    return this.isOn(this.bankAchievement);
  }
}
