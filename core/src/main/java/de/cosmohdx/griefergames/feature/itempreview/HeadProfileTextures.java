package de.cosmohdx.griefergames.feature.itempreview;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;
import org.jetbrains.annotations.Nullable;

public final class HeadProfileTextures {

  private HeadProfileTextures() {
  }

  public static @Nullable HeadTexture fromTexturesValue(String encoded) {
    JsonObject skin = skinObject(encoded);
    if (skin == null || !skin.has("url") || skin.get("url").isJsonNull()) {
      return null;
    }
    String url = skin.get("url").getAsString();
    if (url == null || url.isEmpty()) {
      return null;
    }
    boolean slim = false;
    if (skin.has("metadata") && skin.get("metadata").isJsonObject()) {
      JsonObject metadata = skin.getAsJsonObject("metadata");
      if (metadata.has("model") && !metadata.get("model").isJsonNull()) {
        slim = "slim".equalsIgnoreCase(metadata.get("model").getAsString());
      }
    }
    return new HeadTexture(null, null, url, slim);
  }

  public static @Nullable UUID parseUuid(String raw) {
    if (raw == null) {
      return null;
    }
    String trimmed = raw.trim();
    if (trimmed.isEmpty()) {
      return null;
    }
    if (trimmed.length() == 32) {
      trimmed = trimmed.replaceFirst(
          "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{12})",
          "$1-$2-$3-$4-$5"
      );
    }
    try {
      return UUID.fromString(trimmed);
    } catch (IllegalArgumentException exception) {
      return null;
    }
  }

  public static @Nullable UUID uuidFromIntArray(int[] parts) {
    if (parts == null || parts.length < 4) {
      return null;
    }
    long most = ((long) parts[0] << 32) | (parts[1] & 0xFFFFFFFFL);
    long least = ((long) parts[2] << 32) | (parts[3] & 0xFFFFFFFFL);
    return new UUID(most, least);
  }

  private static @Nullable JsonObject skinObject(String encoded) {
    if (encoded == null || encoded.isEmpty()) {
      return null;
    }
    String json = encoded.trim();
    if (!json.startsWith("{")) {
      try {
        json = new String(Base64.getDecoder().decode(json), StandardCharsets.UTF_8);
      } catch (IllegalArgumentException exception) {
        return null;
      }
    }
    try {
      JsonElement element = JsonParser.parseString(json);
      if (!element.isJsonObject()) {
        return null;
      }
      JsonObject root = element.getAsJsonObject();
      if (root.has("textures") && root.get("textures").isJsonObject()) {
        JsonObject textures = root.getAsJsonObject("textures");
        if (textures.has("SKIN") && textures.get("SKIN").isJsonObject()) {
          return textures.getAsJsonObject("SKIN");
        }
        return null;
      }
      if (root.has("url")) {
        return root;
      }
    } catch (RuntimeException exception) {
      return null;
    }
    return null;
  }
}
