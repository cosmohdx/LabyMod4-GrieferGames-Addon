package de.cosmohdx.griefergames.payload.channel;

import de.cosmohdx.griefergames.payload.ClientPayload;
import de.cosmohdx.griefergames.payload.JsonPayloadDecoder;
import java.io.IOException;
import java.util.Optional;
import net.labymod.api.client.resources.ResourceLocation;

public final class MysteryModPayloadChannel implements IncomingPayloadChannel {

  public static final String NAMESPACE = "mysterymod";
  public static final String PATH = "mm";

  private final ResourceLocation identifier = ResourceLocation.create(NAMESPACE, PATH);
  private final MysteryModPayloadDecoder decoder = new MysteryModPayloadDecoder();

  @Override
  public ResourceLocation identifier() {
    return identifier;
  }

  public void register(String key, JsonPayloadDecoder decoder) {
    this.decoder.register(key, decoder);
  }

  @Override
  public Optional<ClientPayload> decode(byte[] payload) throws IOException {
    return decoder.decode(payload);
  }
}
