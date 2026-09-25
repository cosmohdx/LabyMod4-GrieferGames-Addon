package de.cosmohdx.griefergames.feature.wiki;

import de.cosmohdx.griefergames.feature.wiki.WikiSourceWidget.Display;
import net.labymod.api.Laby;
import net.labymod.api.client.gui.screen.key.Key;
import net.labymod.api.client.gui.screen.widget.widgets.input.ButtonWidget.ButtonSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.KeybindWidget.KeyBindSetting;
import net.labymod.api.configuration.loader.Config;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.util.MethodOrder;

public class WikiConfig extends Config {

  @KeyBindSetting
  private final ConfigProperty<Key> key = new ConfigProperty<>(Key.NONE);

  @MethodOrder(after = "key")
  @ButtonSetting
  public void open() {
    Laby.labyAPI().minecraft().minecraftWindow().displayScreen(new WikiActivity());
  }

  @MethodOrder(after = "open")
  @Display
  public void source() {
  }

  public Key key() {
    Key bound = this.key.get();
    return bound == null ? Key.NONE : bound;
  }
}
