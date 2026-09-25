package de.cosmohdx.griefergames.core.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.cosmohdx.griefergames.feature.chat.SecondChatCategoriesConfig;
import de.cosmohdx.griefergames.feature.chat.SecondChatCategoryConfig;
import org.junit.jupiter.api.Test;

class GrieferGamesConfigMigrationTest {

  @Test
  void movesLegacySettingsIntoTheCurrentTree() {
    JsonObject root = JsonParser.parseString("""
        {
          "enabled": true,
          "chatConfig": {
            "enabled": true,
            "clickToReply": false,
            "preventCommandFailure": true,
            "correctCommandCapitalisation": false,
            "betterIgnoreList": true,
            "showPrefixInDisplayName": false,
            "tabConfig": {
              "enabled": false,
              "chatTabName": "Nebenchat",
              "create": false,
              "manageFilters": false,
              "chatIndicators": true
            },
            "privateChatRight": false,
            "privateChatSound": "PLING",
            "plotChatRight": true,
            "itemRemoverChatRight": true,
            "mobRemoverChatRight": false,
            "realnamePosition": "BOTH",
            "nameHighlightConfig": {
              "enabled": false,
              "additionalHighlightText": "Abge"
            },
            "highlightTPA": false,
            "hideVoteMessages": true,
            "hideNewsMessages": true,
            "hideBlankLines": false,
            "hideSupremeBlankLines": false,
            "ampClantagEnabled": false,
            "ampEnabled": false,
            "ampReplacement": "[X]",
            "showChatTime": true,
            "chatTimeAfterMessage": true,
            "chatTimeFormat": "{h}",
            "itemRemoverLastTimeHover": false,
            "itemRemoverNotification": false,
            "mobRemoverLastTimeHover": false,
            "mobRemoverNotification": true
          },
          "payment": {
            "enabled": true,
            "payChatRight": false,
            "bankChatRight": true,
            "payAchievement": false
          },
          "automations": {
            "enabled": true,
            "autoPortal": true,
            "autoColor": "RED",
            "autoColorCloud": true,
            "colorGradiantCloud": false,
            "afkConfig": {
              "enabled": false,
              "afkTime": 9
            },
            "boosterConfig": {
              "enabled": true,
              "hideBoosterMenu": true
            }
          },
          "friends": {
            "enabled": true,
            "labyChatShowSubServerEnabled": false,
            "discordShowSubServerEnabled": false
          }
        }
        """).getAsJsonObject();

    GrieferGamesConfigMigration.migrate(root, 1);

    assertFalse(root.has("chatConfig"));
    JsonObject chat = root.getAsJsonObject("chat");
    assertTrue(chat.get("enabled").getAsBoolean());
    assertFalse(chat.getAsJsonObject("commands").get("clickToReply").getAsBoolean());
    assertFalse(chat.getAsJsonObject("commands").get("correctCommandCapitalisation").getAsBoolean());
    assertFalse(chat.getAsJsonObject("display").get("showPrefixInDisplayName").getAsBoolean());

    JsonObject secondChat = chat.getAsJsonObject("secondChat");
    assertFalse(secondChat.get("enabled").getAsBoolean());
    assertEquals("Nebenchat", secondChat.get("chatTabName").getAsString());
    assertTrue(secondChat.get("chatIndicators").getAsBoolean());
    JsonObject categories = secondChat.getAsJsonObject("categories");
    assertFalse(categories.getAsJsonObject("privateMessages").get("showInSecondChat").getAsBoolean());
    assertEquals("PLING", categories.getAsJsonObject("privateMessages").get("sound").getAsString());
    assertTrue(categories.getAsJsonObject("plotChat").get("showInSecondChat").getAsBoolean());
    assertTrue(categories.getAsJsonObject("itemRemover").get("showInSecondChat").getAsBoolean());
    assertFalse(categories.getAsJsonObject("mobRemover").get("showInSecondChat").getAsBoolean());
    assertFalse(categories.getAsJsonObject("payments").get("showInSecondChat").getAsBoolean());
    assertTrue(categories.getAsJsonObject("bank").get("showInSecondChat").getAsBoolean());
    assertEquals("BOTH", secondChat.getAsJsonObject("realname").get("position").getAsString());

    assertFalse(chat.getAsJsonObject("mentions").get("enabled").getAsBoolean());
    assertEquals("Abge", chat.getAsJsonObject("mentions").get("additionalHighlightText").getAsString());
    assertFalse(chat.getAsJsonObject("teleport").get("highlightTPA").getAsBoolean());
    assertTrue(chat.getAsJsonObject("filters").get("hideVoteMessages").getAsBoolean());
    assertFalse(chat.getAsJsonObject("filters").get("hideBlankLines").getAsBoolean());
    assertEquals("[X]", chat.getAsJsonObject("magic").get("ampReplacement").getAsString());
    assertTrue(chat.getAsJsonObject("chatTime").get("enabled").getAsBoolean());
    assertTrue(chat.getAsJsonObject("chatTime").get("chatTimeAfterMessage").getAsBoolean());
    assertEquals("{h}", chat.getAsJsonObject("chatTime").get("chatTimeFormat").getAsString());

    assertFalse(root.getAsJsonObject("itemRemover").get("lastTimeHover").getAsBoolean());
    assertFalse(root.getAsJsonObject("itemRemover").get("notification").getAsBoolean());
    assertFalse(root.getAsJsonObject("itemRemover").has("enabled"));
    assertFalse(root.getAsJsonObject("mobRemover").get("lastTimeHover").getAsBoolean());
    assertTrue(root.getAsJsonObject("mobRemover").get("notification").getAsBoolean());

    assertFalse(root.getAsJsonObject("payment").has("payChatRight"));
    assertFalse(root.getAsJsonObject("payment").has("bankChatRight"));
    assertFalse(root.getAsJsonObject("payment").get("payAchievement").getAsBoolean());

    JsonObject automations = root.getAsJsonObject("automations");
    assertFalse(automations.has("afkConfig"));
    assertFalse(automations.has("boosterConfig"));
    assertFalse(automations.has("colorGradiantCloud"));
    assertEquals("RED", automations.getAsJsonObject("chatColor").get("autoColor").getAsString());
    assertTrue(automations.getAsJsonObject("chatColor").get("autoColorCloud").getAsBoolean());
    assertFalse(automations.getAsJsonObject("chatColor").get("colorGradientCloud").getAsBoolean());
    assertFalse(root.getAsJsonObject("afk").get("enabled").getAsBoolean());
    assertEquals(9, root.getAsJsonObject("afk").get("afkTime").getAsInt());
    assertTrue(root.getAsJsonObject("booster").get("hideBoosterMenu").getAsBoolean());

    JsonObject friends = root.getAsJsonObject("friends");
    assertFalse(friends.has("labyChatShowSubServerEnabled"));
    assertFalse(friends.get("showSubServerInLabyChat").getAsBoolean());
    assertFalse(friends.get("showSubServerInDiscord").getAsBoolean());
  }

  @Test
  void disabledChatTurnsExtractedRemoversOff() {
    JsonObject root = JsonParser.parseString("""
        {
          "chatConfig": {
            "enabled": false,
            "itemRemoverLastTimeHover": true
          }
        }
        """).getAsJsonObject();

    GrieferGamesConfigMigration.migrate(root, -1);

    assertFalse(root.getAsJsonObject("chat").get("enabled").getAsBoolean());
    assertFalse(root.getAsJsonObject("itemRemover").get("enabled").getAsBoolean());
    assertTrue(root.getAsJsonObject("itemRemover").get("lastTimeHover").getAsBoolean());
    assertFalse(root.getAsJsonObject("mobRemover").get("enabled").getAsBoolean());
  }

  @Test
  void keepsAnExplicitRemoverSwitch() {
    JsonObject root = JsonParser.parseString("""
        {
          "chatConfig": { "enabled": false },
          "itemRemover": { "enabled": true }
        }
        """).getAsJsonObject();

    GrieferGamesConfigMigration.migrate(root, 1);

    assertTrue(root.getAsJsonObject("itemRemover").get("enabled").getAsBoolean());
  }

  @Test
  void emptyAndCurrentConfigsStayUntouched() {
    JsonObject empty = new JsonObject();
    GrieferGamesConfigMigration.migrate(empty, -1);
    assertEquals(0, empty.size());

    JsonObject current = JsonParser.parseString("""
        { "chat": { "enabled": false, "commands": { "clickToReply": false } } }
        """).getAsJsonObject();
    GrieferGamesConfigMigration.migrate(current, GrieferGamesConfigMigration.CURRENT_VERSION);
    assertTrue(current.getAsJsonObject("chat").has("commands"));
    assertFalse(current.getAsJsonObject("chat").getAsJsonObject("commands").get("clickToReply").getAsBoolean());

    JsonObject once = legacyOnce();
    GrieferGamesConfigMigration.migrate(once, 1);
    GrieferGamesConfigMigration.migrate(once, 1);
    assertEquals(once, migratedTwice(once));
  }

  @Test
  void doesNotOverwriteANewerKey() {
    JsonObject root = JsonParser.parseString("""
        {
          "chat": { "commands": { "clickToReply": false } },
          "chatConfig": { "clickToReply": true, "enabled": true },
          "afk": { "enabled": false },
          "automations": { "afkConfig": { "enabled": true, "afkTime": 3 } }
        }
        """).getAsJsonObject();

    GrieferGamesConfigMigration.migrate(root, 1);

    assertFalse(root.has("chatConfig"));
    assertFalse(root.getAsJsonObject("chat").getAsJsonObject("commands").get("clickToReply").getAsBoolean());
    assertTrue(root.getAsJsonObject("chat").get("enabled").getAsBoolean());
    assertFalse(root.getAsJsonObject("afk").get("enabled").getAsBoolean());
    assertFalse(root.getAsJsonObject("afk").has("afkTime"));
    assertFalse(root.getAsJsonObject("automations").has("afkConfig"));
  }

  @Test
  void categoryPatternsMatchOnlyValidExpressions() {
    SecondChatCategoryConfig category = new SecondChatCategoryConfig("plotChat", true);
    assertFalse(category.matchesPattern("hello"));
    category.patterns().add("priv.*");
    category.patterns().add("   ");
    category.patterns().add("[");
    assertTrue(category.matchesPattern("private message"));
    assertFalse(category.matchesPattern("plot"));
    assertFalse(category.matchesPattern(null));
  }

  @Test
  void categoryIdsSurviveAFreshEmptyConstructor() {
    SecondChatCategoriesConfig categories = new SecondChatCategoriesConfig();
    assertEquals("privateMessages", categories.privateMessages().id());
    assertTrue(categories.privateMessages().showInSecondChat());
    assertEquals("plotChat", categories.plotChat().id());
    assertTrue(categories.plotChat().showInSecondChat());
    assertEquals("itemRemover", categories.itemRemover().id());
    assertFalse(categories.itemRemover().showInSecondChat());
    assertEquals("bank", categories.entries().get(5).id());

    SecondChatCategoryConfig reloaded = new SecondChatCategoryConfig();
    reloaded.bindId("mobRemover");
    assertEquals("mobRemover", reloaded.id());
    assertFalse(reloaded.showInSecondChat());
  }

  private static JsonObject legacyOnce() {
    return JsonParser.parseString("""
        { "chatConfig": { "clickToReply": true, "privateChatRight": true } }
        """).getAsJsonObject();
  }

  private static JsonObject migratedTwice(JsonObject alreadyMigrated) {
    JsonObject copy = alreadyMigrated.deepCopy();
    GrieferGamesConfigMigration.migrate(copy, 1);
    return copy;
  }
}
