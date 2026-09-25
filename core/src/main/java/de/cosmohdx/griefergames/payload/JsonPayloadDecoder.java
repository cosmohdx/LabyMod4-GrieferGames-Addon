package de.cosmohdx.griefergames.payload;

import com.google.gson.JsonElement;
import java.io.IOException;

/**
 * Decodes the JSON body of one {@code mysterymod:mm} message.
 * Return {@code null} to ignore the message.
 */
@FunctionalInterface
public interface JsonPayloadDecoder {

  ClientPayload decode(JsonElement body) throws IOException;
}
