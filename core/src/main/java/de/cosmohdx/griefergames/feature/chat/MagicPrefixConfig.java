package de.cosmohdx.griefergames.feature.chat;

import de.cosmohdx.griefergames.core.GrieferGamesConfig;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.TextFieldWidget.TextFieldSetting;
import net.labymod.api.configuration.loader.Config;
import net.labymod.api.configuration.loader.property.ConfigProperty;

public class MagicPrefixConfig extends Config {

  @SwitchSetting
  private final ConfigProperty<Boolean> ampClantagEnabled = new ConfigProperty<>(true);

  @SwitchSetting
  private final ConfigProperty<Boolean> ampEnabled = new ConfigProperty<>(true);

  @TextFieldSetting
  private final ConfigProperty<String> ampReplacement = new ConfigProperty<>(GrieferGamesConfig.DEFAULT_AMP_REPLACEMENT);

  public boolean replaceClanTags() {
    return this.ampClantagEnabled.get();
  }

  public boolean replacePrefixes() {
    return this.ampEnabled.get();
  }

  public String replacement() {
    return this.ampReplacement.get();
  }
}
