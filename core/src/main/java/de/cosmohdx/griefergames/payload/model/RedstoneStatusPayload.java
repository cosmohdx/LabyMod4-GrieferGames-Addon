package de.cosmohdx.griefergames.payload.model;

import com.google.gson.JsonElement;
import de.cosmohdx.griefergames.payload.ClientPayload;
import java.io.IOException;

/**
 * MysteryMod key {@code redstone}. The legacy client treated status {@code "0"} as active
 * and every other value as inactive.
 */
public record RedstoneStatusPayload(boolean active) implements ClientPayload {

  public static final String ID = "redstone";

  @Override
  public String id() {
    return ID;
  }

  public static RedstoneStatusPayload decode(JsonElement body) throws IOException {
    if (body == null || !body.isJsonObject()) {
      throw new IOException("redstone payload is not a JSON object");
    }
    JsonElement status = body.getAsJsonObject().get("status");
    if (status == null || status.isJsonNull()) {
      throw new IOException("redstone payload has no status");
    }
    return new RedstoneStatusPayload("0".equals(status.getAsString()));
  }
}
