package de.cosmohdx.griefergames.feature.chat;

import java.util.List;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.TagInputWidget.TagInputSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.TagInputWidget.TagCollection;
import net.labymod.api.configuration.loader.Config;
import net.labymod.api.configuration.loader.annotation.Exclude;
import net.labymod.api.configuration.loader.annotation.ShowSettingInParent;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.configuration.settings.annotation.SettingRequires;

/**
 * One message category that can be sent to the second chat.
 *
 * <p>{@code showInSecondChat} is the category toggle (for example private messages on/off).
 * While it is off, {@link SecondChatRouter} ignores the category. {@code patterns} is the list
 * that gets compiled. An empty list is filled once with the defaults from
 * {@link SecondChatCategory}. {@code @SettingRequires} greys that list out while the toggle is
 * off; other extras, such as the private-message sound, stay editable.
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

  @SettingRequires("showInSecondChat")
  @TagInputSetting
  private final ConfigProperty<TagCollection> patterns = new ConfigProperty<>(new TagCollection());

  @Exclude
  private final transient CompiledChatPatterns compiled = new CompiledChatPatterns();

  @Exclude
  private transient boolean defaultsSeeded;

  public SecondChatCategoryConfig() {
    this(null, false);
  }

  public SecondChatCategoryConfig(String id, boolean showByDefault) {
    this.id = id;
    this.showInSecondChat = new ConfigProperty<>(showByDefault);
    this.patterns.addChangeListener(this::compilePatterns);
  }

  public void bindId(String id) {
    if (this.id == null) {
      this.id = id;
    }
    this.seedDefaults();
  }

  public String id() {
    return this.id;
  }

  public boolean showInSecondChat() {
    return this.showInSecondChat.get();
  }

  public ConfigProperty<Boolean> showInSecondChatProperty() {
    return this.showInSecondChat;
  }

  public TagCollection patterns() {
    return this.patterns.get();
  }

  /**
   * Whether an expression in {@link #patterns()} matches this line.
   * Does not consult the category toggle; {@link #routes(String)} does.
   */
  public boolean matchesPattern(String plainText) {
    SecondChatCategory category = SecondChatCategory.find(this.id);
    String prepared = category == null ? plainText : category.prepare(plainText);
    if (prepared == null) {
      return false;
    }
    return this.compiled.matches(this.id, prepared, this.sources());
  }

  /**
   * Category toggle on, and {@link #matchesPattern(String)} is true.
   */
  public boolean routes(String plainText) {
    return this.showInSecondChat() && this.matchesPattern(plainText);
  }

  /**
   * Syntax errors of extra expressions that were skipped on the last compile.
   * Empty when every expression compiled.
   */
  public String invalidPatternHint() {
    return this.compiled.hint(this.id, this.sources());
  }

  void reportInvalidPatternsTo(InvalidPatternReporter reporter) {
    this.compiled.reportTo(reporter);
  }

  private void compilePatterns() {
    this.compiled.matches(this.id, null, this.sources());
  }

  private List<String> sources() {
    return SecondChatCategory.sources(this.patterns.get());
  }

  /**
   * Copies the built-in expressions into an empty list once. A list the user has already edited
   * is left alone for the rest of this session, including when they clear it.
   */
  private void seedDefaults() {
    if (this.defaultsSeeded) {
      return;
    }
    this.defaultsSeeded = true;
    SecondChatCategory category = SecondChatCategory.find(this.id);
    TagCollection patterns = this.patterns.get();
    if (category == null || patterns == null || !patterns.isEmpty()) {
      return;
    }
    for (String pattern : category.defaultPatterns()) {
      patterns.add(pattern);
    }
  }
}
