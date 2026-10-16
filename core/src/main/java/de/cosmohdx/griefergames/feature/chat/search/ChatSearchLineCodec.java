package de.cosmohdx.griefergames.feature.chat.search;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

/**
 * JSON-lines codec for {@link ChatSearchEntry}. The component field is a nested JSON object
 * produced by the Adventure Gson serializer.
 */
public final class ChatSearchLineCodec {

  static final String TIMESTAMP = "timestamp";
  static final String TAB = "tab";
  static final String SUBSERVER = "subserver";
  static final String COMPONENT = "component";
  static final String TEXT = "text";

  private ChatSearchLineCodec() {
  }

  public static String encode(ChatSearchEntry entry) {
    JsonObject object = new JsonObject();
    object.addProperty(TIMESTAMP, entry.timestamp());
    object.addProperty(TAB, entry.tab().name());
    object.addProperty(SUBSERVER, entry.subServer());
    object.add(COMPONENT, parseComponent(entry.componentJson()));
    object.addProperty(TEXT, entry.plainText());
    return object.toString();
  }

  public static ChatSearchEntry decode(String line) {
    if (line == null || line.isBlank()) {
      return null;
    }
    try {
      if (!(JsonParser.parseString(line) instanceof com.google.gson.JsonObject object)) {
        return null;
      }
      if (!object.has(TIMESTAMP) || !object.get(TIMESTAMP).isJsonPrimitive()) {
        return null;
      }
      long timestamp = object.get(TIMESTAMP).getAsLong();
      ChatSearchTab tab = ChatSearchTab.fromName(string(object, TAB));
      String subServer = string(object, SUBSERVER);
      String componentJson = object.has(COMPONENT) && !object.get(COMPONENT).isJsonNull()
          ? object.get(COMPONENT).toString()
          : "";
      String text = string(object, TEXT);
      return new ChatSearchEntry(timestamp, tab, subServer, componentJson, text);
    } catch (JsonSyntaxException | IllegalStateException | NumberFormatException ignored) {
      return null;
    }
  }

  private static String string(JsonObject object, String key) {
    if (!object.has(key) || object.get(key).isJsonNull() || !object.get(key).isJsonPrimitive()) {
      return "";
    }
    return object.get(key).getAsString();
  }

  private static com.google.gson.JsonElement parseComponent(String componentJson) {
    if (componentJson == null || componentJson.isBlank()) {
      return new JsonObject();
    }
    try {
      return JsonParser.parseString(componentJson);
    } catch (JsonSyntaxException ignored) {
      JsonObject fallback = new JsonObject();
      fallback.addProperty("text", componentJson);
      return fallback;
    }
  }
}
