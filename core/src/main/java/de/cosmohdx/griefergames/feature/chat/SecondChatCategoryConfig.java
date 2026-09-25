package de.cosmohdx.griefergames.feature.chat;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.TagInputWidget.TagInputSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.TagInputWidget.TagCollection;
import net.labymod.api.client.gui.screen.widget.widgets.input.TagInputWidget.TagCollection.Tag;
import net.labymod.api.configuration.loader.Config;
import net.labymod.api.configuration.loader.annotation.Exclude;
import net.labymod.api.configuration.loader.annotation.ShowSettingInParent;
import net.labymod.api.configuration.loader.property.ConfigProperty;

/**
 * One message category that can be sent to the second chat.
 *
 * <p>{@code showInSecondChat} is the category toggle (for example private messages on/off).
 * {@code patterns} holds extra regular expressions for the same category. They are stored and
 * can be tested with {@link #matchesPattern(String)}, but no chat module routes on them yet.
 */
public class SecondChatCategoryConfig extends Config {

  /**
   * Not stored in the config file. LabyMod rebuilds nested configs from their empty
   * constructor, so {@link SecondChatCategoriesConfig} stamps the id back onto the live instance.
   */
  @Exclude
  private transient String id;

  @ShowSettingInParent
  @SwitchSetting
  private final ConfigProperty<Boolean> showInSecondChat;

  @TagInputSetting
  private final ConfigProperty<TagCollection> patterns = new ConfigProperty<>(new TagCollection());

  public SecondChatCategoryConfig() {
    this(null, false);
  }

  public SecondChatCategoryConfig(String id, boolean showByDefault) {
    this.id = id;
    this.showInSecondChat = new ConfigProperty<>(showByDefault);
  }

  public void bindId(String id) {
    if (this.id == null) {
      this.id = id;
    }
  }

  public String id() {
    return this.id;
  }

  public boolean showInSecondChat() {
    return this.showInSecondChat.get();
  }

  public TagCollection patterns() {
    return this.patterns.get();
  }

  public boolean matchesPattern(String plainText) {
    if (plainText == null || this.patterns.get() == null) {
      return false;
    }
    for (Tag tag : this.patterns.get().getTags()) {
      String content = tag.getContent();
      if (content == null || content.isBlank()) {
        continue;
      }
      try {
        if (Pattern.compile(content).matcher(plainText).find()) {
          return true;
        }
      } catch (PatternSyntaxException ignored) {
        // Keep the user's text; a later editor can surface the syntax error.
      }
    }
    return false;
  }
}
