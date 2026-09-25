package de.cosmohdx.griefergames.payload.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.cosmohdx.griefergames.payload.ClientPayload;
import java.io.IOException;
import java.util.UUID;

/**
 * MysteryMod key {@code user_subtitle}. Only the first array entry is used, and only
 * {@code text} plus {@code targetId}. The subtitle size is not part of the payload the
 * old client read.
 */
public record UserSubtitlePayload(UUID targetId, String text) implements ClientPayload {

  public static final String ID = "user_subtitle";

  public UserSubtitlePayload {
    if (targetId == null || text == null) {
      throw new IllegalArgumentException("targetId and text are required");
    }
  }

  @Override
  public String id() {
    return ID;
  }

  /**
   * @return {@code null} when the message has no usable entry (empty array or missing fields)
   */
  public static UserSubtitlePayload decode(JsonElement body) throws IOException {
    if (body == null || !body.isJsonArray() || body.getAsJsonArray().isEmpty()) {
      return null;
    }
    JsonElement first = body.getAsJsonArray().get(0);
    if (!first.isJsonObject()) {
      throw new IOException("user_subtitle entry is not a JSON object");
    }
    JsonObject data = first.getAsJsonObject();
    if (!data.has("text") || !data.has("targetId")
        || data.get("text").isJsonNull() || data.get("targetId").isJsonNull()) {
      return null;
    }
    try {
      return new UserSubtitlePayload(
          UUID.fromString(data.get("targetId").getAsString()),
          data.get("text").getAsString()
      );
    } catch (IllegalArgumentException exception) {
      throw new IOException("user_subtitle targetId is not a UUID", exception);
    }
  }
}
