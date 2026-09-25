package de.cosmohdx.griefergames.payload.channel;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import de.cosmohdx.griefergames.payload.ClientPayload;
import de.cosmohdx.griefergames.payload.JsonPayloadDecoder;
import de.cosmohdx.griefergames.payload.model.MysteryModMessage;
import de.cosmohdx.griefergames.payload.model.RedstoneStatusPayload;
import de.cosmohdx.griefergames.payload.model.UserSubtitlePayload;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import net.labymod.serverapi.api.payload.io.PayloadReader;

/**
 * Decodes {@code mysterymod:mm}. Two strings, each a Minecraft VarInt length plus UTF-8,
 * then a JSON body. Same framing as the legacy LabyMod plugin channel, read with
 * {@link PayloadReader}.
 */
public final class MysteryModPayloadDecoder {

  private final Gson gson = new Gson();
  private final Map<String, JsonPayloadDecoder> codecs = new LinkedHashMap<>();

  public MysteryModPayloadDecoder() {
    register(RedstoneStatusPayload.ID, RedstoneStatusPayload::decode);
    register(UserSubtitlePayload.ID, UserSubtitlePayload::decode);
  }

  public void register(String key, JsonPayloadDecoder decoder) {
    synchronized (codecs) {
      JsonPayloadDecoder previous = codecs.putIfAbsent(key, decoder);
      if (previous != null) {
        throw new IllegalArgumentException("Duplicate MysteryMod payload key: " + key);
      }
    }
  }

  public Optional<ClientPayload> decode(byte[] payload) throws IOException {
    if (payload == null) {
      throw new IOException("MysteryMod payload is null");
    }
    String key;
    String json;
    try {
      PayloadReader reader = new PayloadReader(payload);
      key = reader.readString();
      json = reader.readString();
    } catch (RuntimeException exception) {
      throw new IOException("Could not read MysteryMod payload", exception);
    }

    JsonElement body;
    try {
      body = gson.fromJson(json, JsonElement.class);
    } catch (RuntimeException exception) {
      throw new IOException("Invalid MysteryMod JSON for key " + key, exception);
    }

    JsonPayloadDecoder decoder;
    synchronized (codecs) {
      decoder = codecs.get(key);
    }
    if (decoder == null) {
      return Optional.of(new MysteryModMessage(key, json));
    }
    ClientPayload decoded = decoder.decode(body);
    return Optional.ofNullable(decoded);
  }
}
