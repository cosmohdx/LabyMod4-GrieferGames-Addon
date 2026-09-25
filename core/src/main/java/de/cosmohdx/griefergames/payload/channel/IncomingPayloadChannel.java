package de.cosmohdx.griefergames.payload.channel;

import de.cosmohdx.griefergames.payload.ClientPayload;
import java.io.IOException;
import java.util.Optional;
import net.labymod.api.client.resources.ResourceLocation;

/**
 * One plugin-message channel. Implementations know the wire format and produce typed payloads.
 */
public interface IncomingPayloadChannel {

  ResourceLocation identifier();

  /**
   * @return empty when the message is valid but intentionally ignored
   */
  Optional<ClientPayload> decode(byte[] payload) throws IOException;
}
