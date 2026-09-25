package de.cosmohdx.griefergames.feature.itemlist;

import net.labymod.api.client.gui.screen.key.Key;
import net.labymod.api.client.gui.screen.widget.widgets.input.ButtonWidget.ButtonSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.KeybindWidget.KeyBindSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import de.cosmohdx.griefergames.feature.itemlist.ItemListSourceWidget.Display;
import net.labymod.api.configuration.loader.Config;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.util.MethodOrder;

public class ItemListConfig extends Config {

  @SwitchSetting
  private final ConfigProperty<Boolean> autoUpdate = new ConfigProperty<>(true);

  @KeyBindSetting
  private final ConfigProperty<Key> key = new ConfigProperty<>(Key.NONE);

  @MethodOrder(after = "key")
  @ButtonSetting
  public void open() {
    ItemListActivity.open();
  }

  @MethodOrder(after = "open")
  @Display
  public void source() {
  }

  public boolean autoUpdate() {
    return !Boolean.FALSE.equals(this.autoUpdate.get());
  }

  public Key key() {
    Key bound = this.key.get();
    return bound == null ? Key.NONE : bound;
  }
}
