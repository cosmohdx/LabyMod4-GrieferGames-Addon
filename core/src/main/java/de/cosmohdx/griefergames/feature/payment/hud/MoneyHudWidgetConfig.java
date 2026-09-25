package de.cosmohdx.griefergames.feature.payment.hud;

import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidgetConfig;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.configuration.loader.property.ConfigProperty;

public class MoneyHudWidgetConfig extends TextHudWidgetConfig {

  @SwitchSetting
  private final ConfigProperty<Boolean> showCents = new ConfigProperty<>(true);

  @SwitchSetting
  private final ConfigProperty<Boolean> compact = new ConfigProperty<>(false);

  @SwitchSetting
  private final ConfigProperty<Boolean> hideWhenZero = new ConfigProperty<>(false);

  public ConfigProperty<Boolean> showCents() {
    return this.showCents;
  }

  public ConfigProperty<Boolean> compact() {
    return this.compact;
  }

  public ConfigProperty<Boolean> hideWhenZero() {
    return this.hideWhenZero;
  }
}
