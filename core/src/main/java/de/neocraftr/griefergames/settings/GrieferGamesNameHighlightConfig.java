package de.neocraftr.griefergames.settings;

import de.neocraftr.griefergames.enums.Sounds;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.TextFieldWidget.TextFieldSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.color.ColorPickerWidget.ColorPickerSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.dropdown.DropdownWidget.DropdownSetting;
import net.labymod.api.configuration.loader.Config;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.util.Color;

public class GrieferGamesNameHighlightConfig extends Config {

  //@ParentSwitch
  @SwitchSetting
  private final ConfigProperty<Boolean> enabled = new ConfigProperty<Boolean>(true);

  @ColorPickerSetting
  private final ConfigProperty<Color> mentionColor = new ConfigProperty<>(Color.ofRGB(121, 178, 255));

  @DropdownSetting
  private final ConfigProperty<Sounds> mentionSound = new ConfigProperty<>(Sounds.NONE);

  @TextFieldSetting
  private final ConfigProperty<String> additionalHighlightText = new ConfigProperty<>("");

  public boolean isEnabled() {
    return enabled.get();
  }

  public Color getMentionColor() {
    return mentionColor.get();
  }

  public Sounds getMentionSound() {
    return mentionSound.get();
  }

  public String getAdditionalHighlightText() {
    return additionalHighlightText.get();
  }

}
