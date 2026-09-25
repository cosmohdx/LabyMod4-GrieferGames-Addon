package de.cosmohdx.griefergames.core.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Map;

/**
 * Rewrites saved addon settings into the current tree.
 *
 * <p>Version 2 groups each feature into its own sub-config and moves second-chat routing
 * into per-category entries. Version 3 folds the item and mob removers into one feature
 * and one second-chat category. Values are copied; only the path changes.
 */
public final class GrieferGamesConfigMigration {

  public static final int CURRENT_VERSION = 3;

  private GrieferGamesConfigMigration() {
  }

  public static void migrate(JsonObject root, int usedVersion) {
    if (root == null || usedVersion >= CURRENT_VERSION) {
      return;
    }
    if (usedVersion < 2) {
      migrateToVersion2(root);
    }
    if (usedVersion < 3) {
      migrateToVersion3(root);
    }
  }

  private static void migrateToVersion2(JsonObject root) {
    JsonObject legacyChat = removeObject(root, "chatConfig");
    JsonObject chat = object(root, "chat");
    if (chat == null && legacyChat != null) {
      chat = legacyChat;
      root.add("chat", chat);
    } else if (chat != null && legacyChat != null) {
      copyMissing(legacyChat, chat);
    }

    if (chat != null) {
      restructureChat(chat, root);
    }
    movePaymentRouting(root);
    restructureAutomations(root);
    restructureFriends(root);
  }

  private static void restructureChat(JsonObject chat, JsonObject root) {
    JsonObject commands = child(chat, "commands");
    move(chat, commands, "clickToReply");
    move(chat, commands, "preventCommandFailure");
    move(chat, commands, "correctCommandCapitalisation");
    move(chat, commands, "betterIgnoreList");
    dropIfEmpty(chat, "commands");

    JsonObject display = child(chat, "display");
    move(chat, display, "showPrefixInDisplayName");
    dropIfEmpty(chat, "display");

    JsonObject secondChat = object(chat, "tabConfig");
    if (secondChat != null) {
      chat.remove("tabConfig");
      JsonObject existing = object(chat, "secondChat");
      if (existing == null) {
        chat.add("secondChat", secondChat);
      } else {
        copyMissing(secondChat, existing);
        secondChat = existing;
      }
    } else {
      secondChat = object(chat, "secondChat");
    }
    if (secondChat == null) {
      secondChat = new JsonObject();
    }

    JsonObject categories = child(secondChat, "categories");
    moveShowInSecondChat(chat, categories, "privateChatRight", "privateMessages");
    move(chat, child(categories, "privateMessages"), "privateChatSound", "sound");
    dropIfEmpty(categories, "privateMessages");
    moveShowInSecondChat(chat, categories, "plotChatRight", "plotChat");
    moveShowInSecondChat(chat, categories, "itemRemoverChatRight", "itemRemover");
    moveShowInSecondChat(chat, categories, "mobRemoverChatRight", "mobRemover");

    JsonObject realname = child(secondChat, "realname");
    move(chat, realname, "realnamePosition", "position");
    dropIfEmpty(secondChat, "realname");
    dropIfEmpty(secondChat, "categories");
    if (secondChat.size() > 0) {
      chat.add("secondChat", secondChat);
    }

    renameObject(chat, "nameHighlightConfig", "mentions");

    JsonObject teleport = child(chat, "teleport");
    move(chat, teleport, "highlightTPA");
    dropIfEmpty(chat, "teleport");

    JsonObject filters = child(chat, "filters");
    move(chat, filters, "hideVoteMessages");
    move(chat, filters, "hideNewsMessages");
    move(chat, filters, "hideBlankLines");
    move(chat, filters, "hideSupremeBlankLines");
    dropIfEmpty(chat, "filters");

    JsonObject magic = child(chat, "magic");
    move(chat, magic, "ampClantagEnabled");
    move(chat, magic, "ampEnabled");
    move(chat, magic, "ampReplacement");
    dropIfEmpty(chat, "magic");

    JsonObject chatTime = child(chat, "chatTime");
    move(chat, chatTime, "showChatTime", "enabled");
    move(chat, chatTime, "chatTimeAfterMessage");
    move(chat, chatTime, "chatTimeFormat");
    dropIfEmpty(chat, "chatTime");

    boolean chatDisabled = chat.has("enabled") && !asBoolean(chat.get("enabled"), true);
    extractRemover(root, chat, "itemRemover", "itemRemoverLastTimeHover", "itemRemoverNotification", chatDisabled);
    extractRemover(root, chat, "mobRemover", "mobRemoverLastTimeHover", "mobRemoverNotification", chatDisabled);
  }

  private static void movePaymentRouting(JsonObject root) {
    JsonObject payment = object(root, "payment");
    if (payment == null) {
      return;
    }
    boolean pay = payment.has("payChatRight");
    boolean bank = payment.has("bankChatRight");
    if (!pay && !bank) {
      return;
    }
    JsonObject chat = object(root, "chat");
    if (chat == null) {
      chat = new JsonObject();
      root.add("chat", chat);
    }
    JsonObject categories = child(child(chat, "secondChat"), "categories");
    if (pay) {
      move(payment, child(categories, "payments"), "payChatRight", "showInSecondChat");
    }
    if (bank) {
      move(payment, child(categories, "bank"), "bankChatRight", "showInSecondChat");
    }
  }

  private static void restructureAutomations(JsonObject root) {
    JsonObject automations = object(root, "automations");
    if (automations == null) {
      return;
    }
    hoist(root, automations, "afkConfig", "afk");
    hoist(root, automations, "boosterConfig", "booster");

    JsonObject chatColor = child(automations, "chatColor");
    move(automations, chatColor, "autoColor");
    move(automations, chatColor, "autoColorCloud");
    move(automations, chatColor, "autoColorCloudColor");
    move(automations, chatColor, "colorGradiantCloud", "colorGradientCloud");
    dropIfEmpty(automations, "chatColor");
  }

  private static void restructureFriends(JsonObject root) {
    JsonObject friends = object(root, "friends");
    if (friends == null) {
      return;
    }
    move(friends, friends, "labyChatShowSubServerEnabled", "showSubServerInLabyChat");
    move(friends, friends, "discordShowSubServerEnabled", "showSubServerInDiscord");
  }

  private static void migrateToVersion3(JsonObject root) {
    JsonObject item = object(root, "itemRemover");
    JsonObject mob = object(root, "mobRemover");
    if (item != null || mob != null) {
      JsonObject remover = object(root, "remover");
      if (remover == null) {
        remover = new JsonObject();
        root.add("remover", remover);
      }
      absorbRemover(remover, item);
      absorbRemover(remover, mob);
      root.remove("itemRemover");
      root.remove("mobRemover");
    }
    mergeRemoverCategory(root);
  }

  private static void absorbRemover(JsonObject into, JsonObject from) {
    if (from == null) {
      return;
    }
    copyMissing(from, into);
    orBoolean(into, from, "enabled");
    orBoolean(into, from, "lastTimeHover");
    orBoolean(into, from, "notification");
  }

  private static void mergeRemoverCategory(JsonObject root) {
    JsonObject chat = object(root, "chat");
    JsonObject secondChat = chat == null ? null : object(chat, "secondChat");
    JsonObject categories = secondChat == null ? null : object(secondChat, "categories");
    if (categories == null) {
      return;
    }
    JsonObject item = object(categories, "itemRemover");
    JsonObject mob = object(categories, "mobRemover");
    if (item == null && mob == null) {
      return;
    }
    JsonObject remover = object(categories, "remover");
    if (remover == null) {
      remover = new JsonObject();
      categories.add("remover", remover);
    }
    absorbCategory(remover, item);
    absorbCategory(remover, mob);
    categories.remove("itemRemover");
    categories.remove("mobRemover");
  }

  private static void absorbCategory(JsonObject into, JsonObject from) {
    if (from == null) {
      return;
    }
    copyMissing(from, into);
    orBoolean(into, from, "showInSecondChat");
  }

  private static void orBoolean(JsonObject into, JsonObject from, String key) {
    if (!from.has(key)) {
      return;
    }
    if (!into.has(key)) {
      into.add(key, from.get(key));
    } else if (asBoolean(from.get(key), false)) {
      into.addProperty(key, true);
    }
  }

  private static void extractRemover(JsonObject root, JsonObject chat, String featureKey, String hoverKey,
      String notificationKey, boolean chatDisabled) {
    JsonObject feature = object(root, featureKey);
    boolean created = feature == null;
    if (created) {
      feature = new JsonObject();
    }
    boolean touched = false;
    touched |= move(chat, feature, hoverKey, "lastTimeHover");
    touched |= move(chat, feature, notificationKey, "notification");
    if (chatDisabled && !feature.has("enabled")) {
      feature.addProperty("enabled", false);
      touched = true;
    }
    if (touched) {
      root.add(featureKey, feature);
    }
  }

  private static void moveShowInSecondChat(JsonObject chat, JsonObject categories, String oldKey, String categoryId) {
    if (!chat.has(oldKey)) {
      return;
    }
    move(chat, child(categories, categoryId), oldKey, "showInSecondChat");
  }

  private static void hoist(JsonObject root, JsonObject from, String oldKey, String newKey) {
    if (!from.has(oldKey)) {
      return;
    }
    JsonElement value = from.remove(oldKey);
    if (!root.has(newKey)) {
      root.add(newKey, value);
    }
  }

  private static void renameObject(JsonObject parent, String oldKey, String newKey) {
    if (!parent.has(oldKey) || parent.has(newKey)) {
      if (parent.has(oldKey) && parent.has(newKey)) {
        parent.remove(oldKey);
      }
      return;
    }
    parent.add(newKey, parent.remove(oldKey));
  }

  private static boolean move(JsonObject from, JsonObject to, String key) {
    return move(from, to, key, key);
  }

  private static boolean move(JsonObject from, JsonObject to, String oldKey, String newKey) {
    if (from == null || !from.has(oldKey)) {
      return false;
    }
    JsonElement value = from.remove(oldKey);
    if (to != null && !to.has(newKey)) {
      to.add(newKey, value);
    }
    return true;
  }

  private static void copyMissing(JsonObject from, JsonObject to) {
    for (Map.Entry<String, JsonElement> entry : from.entrySet()) {
      if (!to.has(entry.getKey())) {
        to.add(entry.getKey(), entry.getValue());
      }
    }
  }

  private static JsonObject child(JsonObject parent, String key) {
    JsonObject existing = object(parent, key);
    if (existing != null) {
      return existing;
    }
    JsonObject created = new JsonObject();
    parent.add(key, created);
    return created;
  }

  private static void dropIfEmpty(JsonObject parent, String key) {
    JsonObject child = object(parent, key);
    if (child != null && child.size() == 0) {
      parent.remove(key);
    }
  }

  private static JsonObject object(JsonObject parent, String key) {
    if (parent == null || !parent.has(key) || !parent.get(key).isJsonObject()) {
      return null;
    }
    return parent.getAsJsonObject(key);
  }

  private static JsonObject removeObject(JsonObject parent, String key) {
    JsonObject value = object(parent, key);
    if (value != null) {
      parent.remove(key);
    }
    return value;
  }

  private static boolean asBoolean(JsonElement element, boolean fallback) {
    if (element == null || !element.isJsonPrimitive() || !element.getAsJsonPrimitive().isBoolean()) {
      return fallback;
    }
    return element.getAsBoolean();
  }
}
