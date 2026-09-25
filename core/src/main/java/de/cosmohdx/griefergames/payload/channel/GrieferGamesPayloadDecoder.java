package de.cosmohdx.griefergames.payload.channel;

import de.cosmohdx.griefergames.payload.ClientPayload;
import de.cosmohdx.griefergames.payload.PayloadCodec;
import de.cosmohdx.griefergames.payload.model.AccountBalancePayload;
import de.cosmohdx.griefergames.payload.model.BankBalancePayload;
import de.cosmohdx.griefergames.payload.model.BlockOfTheDayPayload;
import de.cosmohdx.griefergames.payload.model.BoosterPayload;
import de.cosmohdx.griefergames.payload.model.ClearLagPayload;
import de.cosmohdx.griefergames.payload.model.EntityRemoverPayload;
import de.cosmohdx.griefergames.payload.model.LootTableProgressPayload;
import de.cosmohdx.griefergames.payload.model.PlotChatConfigurationPayload;
import de.cosmohdx.griefergames.payload.model.UnknownPayload;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Decodes {@code griefergames:main}. The server writes a modified-UTF id with
 * {@link java.io.DataOutputStream#writeUTF} and then the payload fields with the same stream
 * (big-endian). This is not the LabyMod VarInt string format.
 */
public final class GrieferGamesPayloadDecoder {

  private final Map<String, PayloadCodec<?>> codecs = new LinkedHashMap<>();

  public GrieferGamesPayloadDecoder() {
    register(PayloadCodec.of(AccountBalancePayload.ID, AccountBalancePayload::decode));
    register(PayloadCodec.of(BankBalancePayload.ID, BankBalancePayload::decode));
    register(PayloadCodec.of(BoosterPayload.ID, BoosterPayload::decode));
    register(PayloadCodec.of(PlotChatConfigurationPayload.ID, PlotChatConfigurationPayload::decode));
    register(PayloadCodec.of(ClearLagPayload.ID, ClearLagPayload::decode));
    register(PayloadCodec.of(EntityRemoverPayload.ID, EntityRemoverPayload::decode));
    register(PayloadCodec.of(BlockOfTheDayPayload.ID, BlockOfTheDayPayload::decode));
    register(PayloadCodec.of(LootTableProgressPayload.ID, LootTableProgressPayload::decode));
  }

  public void register(PayloadCodec<?> codec) {
    synchronized (codecs) {
      PayloadCodec<?> previous = codecs.putIfAbsent(codec.id(), codec);
      if (previous != null) {
        throw new IllegalArgumentException("Duplicate GrieferGames payload id: " + codec.id());
      }
    }
  }

  public ClientPayload decode(byte[] payload) throws IOException {
    if (payload == null) {
      throw new IOException("GrieferGames payload is null");
    }
    DataInputStream input = new DataInputStream(new ByteArrayInputStream(payload));
    String id = input.readUTF();
    PayloadCodec<?> codec;
    synchronized (codecs) {
      codec = codecs.get(id);
    }
    if (codec == null) {
      return new UnknownPayload(id, input.readAllBytes());
    }
    return codec.decode(input);
  }
}
