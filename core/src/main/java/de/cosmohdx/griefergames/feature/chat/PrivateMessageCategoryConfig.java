package de.cosmohdx.griefergames.feature.chat;

import net.labymod.api.client.gui.screen.widget.widgets.input.dropdown.DropdownWidget.DropdownSetting;
import net.labymod.api.configuration.loader.property.ConfigProperty;

public class PrivateMessageCategoryConfig extends SecondChatCategoryConfig {

  @DropdownSetting
  private final ConfigProperty<Sounds> sound = new ConfigProperty<>(Sounds.POP);

  public PrivateMessageCategoryConfig() {
    super("privateMessages", true);
  }

  public Sounds sound() {
    return this.sound.get();
  }
}
