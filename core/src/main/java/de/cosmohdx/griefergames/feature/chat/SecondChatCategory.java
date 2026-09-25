package de.cosmohdx.griefergames.feature.chat;

import java.util.ArrayList;
import java.util.List;
import net.labymod.api.client.gui.screen.widget.widgets.input.TagInputWidget.TagCollection;
import net.labymod.api.client.gui.screen.widget.widgets.input.TagInputWidget.TagCollection.Tag;

/**
 * Built-in second-chat categories and the GrieferGames formats they already recognised.
 *
 * <p>To add a category:
 * <ol>
 *   <li>Add a constant here: config id, how the plain text is prepared, and the default
 *       patterns.</li>
 *   <li>Add a field and accessor in {@link SecondChatCategoriesConfig} and include it in
 *       {@link SecondChatCategoriesConfig#entries()}.</li>
 *   <li>Add {@code settings.chat.secondChat.categories.<id>} in {@code de_de.json} and
 *       {@code en_us.json} ({@code name}, {@code description}, {@code showInSecondChat},
 *       {@code patterns}).</li>
 * </ol>
 *
 * <p>The config list is what {@link SecondChatRouter} compiles. {@link SecondChatCategoryConfig}
 * copies these defaults into an empty list the first time the category is used, so older configs
 * keep the previous detection. After that the list is the source of truth: users can add, edit
 * and remove expressions. A disabled category does not compile its list. The defaults are the
 * detectors the chat modules used before routing moved here; those modules still parse the same
 * messages when they need capture groups for sounds, notifications or logs.
 */
public enum SecondChatCategory {

  PRIVATE_MESSAGES("privateMessages", TextRule.PRIVATE_MESSAGE, List.of(
      "\\[" + SecondChatFormats.RANK_NAME + " -> (mir|me)\\] (.*)$",
      "\\[(mir|me) -> " + SecondChatFormats.RANK_NAME + "\\] (.*)$"
  )),
  PLOT_CHAT("plotChat", TextRule.RAW, List.of(
      "^\\[Plot-Chat\\]"
  )),
  REMOVER("remover", TextRule.RAW, List.of(
      "^\\[GrieferGames\\] Warnung! Die auf dem Boden liegenden Items werden in "
          + "([0-9]+) Sekunden entfernt!$",
      "^\\[GrieferGames\\] Es wurden ([0-9]+) auf dem Boden liegende Items entfernt!$",
      "^\\[MobRemover\\] Achtung! In ([0-9]+) Minuten? werden alle Tiere gelöscht\\.$",
      "^\\[MobRemover\\] Es wurden ([0-9]+) Tiere entfernt\\.$"
  )),
  PAYMENTS("payments", TextRule.RAW, List.of(
      "^" + SecondChatFormats.RANK_NAME + " hat dir \\$" + SecondChatFormats.MONEY
          + " gegeben\\.$",
      "^Du hast " + SecondChatFormats.RANK_NAME + " \\$" + SecondChatFormats.MONEY
          + " gegeben\\.$",
      "\\$" + SecondChatFormats.MONEY + " wurde zu deinem Konto hinzugefügt\\.$",
      "^Kontostand: "
  )),
  BANK("bank", TextRule.RAW, List.of(
      "^\\[Bank\\] "
  ));

  private final String id;
  private final TextRule textRule;
  private final List<String> defaultPatterns;

  SecondChatCategory(String id, TextRule textRule, List<String> defaultPatterns) {
    this.id = id;
    this.textRule = textRule;
    this.defaultPatterns = List.copyOf(defaultPatterns);
  }

  public String id() {
    return this.id;
  }

  public List<String> defaultPatterns() {
    return this.defaultPatterns;
  }

  public static List<String> ids() {
    List<String> ids = new ArrayList<>();
    for (SecondChatCategory category : values()) {
      ids.add(category.id);
    }
    return List.copyOf(ids);
  }

  public static SecondChatCategory find(String id) {
    if (id == null) {
      return null;
    }
    for (SecondChatCategory category : values()) {
      if (category.id.equals(id)) {
        return category;
      }
    }
    return null;
  }

  /**
   * Plain text passed to this category's patterns.
   *
   * @return the text to match, or {@code null} when this category ignores the message
   */
  public String prepare(String plainText) {
    return this.textRule.prepare(plainText);
  }

  static List<String> sources(TagCollection patterns) {
    List<String> sources = new ArrayList<>();
    if (patterns == null) {
      return sources;
    }
    for (Tag tag : patterns.getTags()) {
      String content = tag.getContent();
      if (content == null || content.isBlank() || sources.contains(content)) {
        continue;
      }
      sources.add(content);
    }
    return sources;
  }

  /**
   * How a category reads the chat line before its patterns run.
   *
   * <p>{@link #PRIVATE_MESSAGE} keeps the old private-message guard: a line that contains the
   * global-chat separator is ignored, and leading section signs are stripped first.
   */
  enum TextRule {
    RAW,
    PRIVATE_MESSAGE;

    String prepare(String plainText) {
      if (plainText == null) {
        return null;
      }
      if (this != PRIVATE_MESSAGE) {
        return plainText;
      }
      if (plainText.indexOf('\u00BB') >= 0) {
        return null;
      }
      String stripped = plainText;
      while (stripped.length() >= 2 && stripped.charAt(0) == '\u00A7') {
        stripped = stripped.substring(2);
      }
      return stripped;
    }
  }
}
