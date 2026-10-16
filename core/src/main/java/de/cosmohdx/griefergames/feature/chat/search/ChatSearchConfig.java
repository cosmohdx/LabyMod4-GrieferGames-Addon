package de.cosmohdx.griefergames.feature.chat.search;

import de.cosmohdx.griefergames.GrieferGames;
import de.cosmohdx.griefergames.core.config.FeatureConfig;
import net.labymod.api.client.gui.screen.key.Key;
import net.labymod.api.client.gui.screen.widget.widgets.input.ButtonWidget.ButtonSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.KeybindWidget.KeyBindSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.SliderWidget.SliderSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.util.MethodOrder;

public class ChatSearchConfig extends FeatureConfig {

  @SliderSetting(min = ChatSearchRetention.MIN_MINUTES, max = ChatSearchRetention.MAX_MINUTES, steps = 5)
  private final ConfigProperty<Integer> retentionMinutes = new ConfigProperty<>(ChatSearchRetention.DEFAULT_MINUTES);

  @SliderSetting(min = 1_000, max = 50_000, steps = 1_000)
  private final ConfigProperty<Integer> maxLines = new ConfigProperty<>(ChatSearchRetention.DEFAULT_MAX_LINES);

  @SwitchSetting
  private final ConfigProperty<Boolean> onlyOnGrieferGames = new ConfigProperty<>(true);

  @KeyBindSetting
  private final ConfigProperty<Key> key = new ConfigProperty<>(Key.NONE);

  @MethodOrder(after = "key")
  @ButtonSetting
  public void open() {
    ChatSearchActivity.open();
  }

  @MethodOrder(after = "open")
  @ButtonSetting
  public void deleteNow() {
    GrieferGames addon = GrieferGames.get();
    if (addon != null && addon.chatSearch() != null) {
      addon.chatSearch().deleteNow();
    }
  }

  public int retentionMinutes() {
    Integer value = this.retentionMinutes.get();
    return ChatSearchRetention.clampMinutes(value == null ? ChatSearchRetention.DEFAULT_MINUTES : value);
  }

  public int maxLines() {
    Integer value = this.maxLines.get();
    return ChatSearchRetention.clampMaxLines(value == null ? ChatSearchRetention.DEFAULT_MAX_LINES : value);
  }

  public boolean onlyOnGrieferGames() {
    return !Boolean.FALSE.equals(this.onlyOnGrieferGames.get());
  }

  public Key key() {
    Key bound = this.key.get();
    return bound == null ? Key.NONE : bound;
  }
}
