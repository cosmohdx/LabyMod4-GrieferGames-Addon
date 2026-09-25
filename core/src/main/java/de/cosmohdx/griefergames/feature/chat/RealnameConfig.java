package de.cosmohdx.griefergames.feature.chat;

import net.labymod.api.client.gui.screen.widget.widgets.input.dropdown.DropdownWidget.DropdownSetting;
import net.labymod.api.configuration.loader.Config;
import net.labymod.api.configuration.loader.property.ConfigProperty;

public class RealnameConfig extends Config {

  @DropdownSetting
  private final ConfigProperty<RealnamePosition> position = new ConfigProperty<>(RealnamePosition.DEFAULT);

  public RealnamePosition position() {
    return this.position.get();
  }
}
