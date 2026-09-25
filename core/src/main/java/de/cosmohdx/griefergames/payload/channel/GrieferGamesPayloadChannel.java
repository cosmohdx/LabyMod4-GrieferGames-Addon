package de.cosmohdx.griefergames.payload.channel;

import de.cosmohdx.griefergames.payload.ClientPayload;
import de.cosmohdx.griefergames.payload.PayloadCodec;
import java.io.IOException;
import java.util.Optional;
import net.labymod.api.client.resources.ResourceLocation;

public final class GrieferGamesPayloadChannel implements IncomingPayloadChannel {

  public static final String NAMESPACE = "griefergames";
  public static final String PATH = "main";

  private final ResourceLocation identifier = ResourceLocation.create(NAMESPACE, PATH);
  private final GrieferGamesPayloadDecoder decoder = new GrieferGamesPayloadDecoder();

  @Override
  public ResourceLocation identifier() {
    return identifier;
  }

  public void register(PayloadCodec<?> codec) {
    decoder.register(codec);
  }

  @Override
  public Optional<ClientPayload> decode(byte[] payload) throws IOException {
    return Optional.of(decoder.decode(payload));
  }
}
