package de.cosmohdx.griefergames.feature.chat;

import de.cosmohdx.griefergames.core.config.FeatureConfig;
import net.labymod.api.client.gui.screen.widget.widgets.input.TextFieldWidget.TextFieldSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.color.ColorPickerWidget.ColorPickerSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.dropdown.DropdownWidget.DropdownSetting;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.util.Color;

public class MentionConfig extends FeatureConfig {

  @ColorPickerSetting
  private final ConfigProperty<Color> mentionColor = new ConfigProperty<>(Color.ofRGB(121, 178, 255));

  @DropdownSetting
  private final ConfigProperty<Sounds> mentionSound = new ConfigProperty<>(Sounds.NONE);

  @TextFieldSetting
  private final ConfigProperty<String> additionalHighlightText = new ConfigProperty<>("");

  public Color mentionColor() {
    return this.mentionColor.get();
  }

  public Sounds mentionSound() {
    return this.isEnabled() ? this.mentionSound.get() : Sounds.NONE;
  }

  public String additionalHighlightText() {
    return this.additionalHighlightText.get();
  }
}
