package de.cosmohdx.griefergames.payload;

import de.cosmohdx.griefergames.GrieferGames;
import de.cosmohdx.griefergames.payload.channel.GrieferGamesPayloadChannel;
import de.cosmohdx.griefergames.payload.channel.IncomingPayloadChannel;
import de.cosmohdx.griefergames.payload.channel.MysteryModPayloadChannel;
import de.cosmohdx.griefergames.payload.model.MysteryModMessage;
import de.cosmohdx.griefergames.payload.model.UnknownPayload;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import net.labymod.api.Laby;
import net.labymod.api.client.resources.ResourceLocation;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.network.server.NetworkPayloadEvent;
import net.labymod.api.event.client.network.server.NetworkPayloadEvent.Side;

/**
 * Registers the GrieferGames and MysteryMod plugin channels and turns incoming bytes into
 * {@link ClientPayload}s.
 *
 * <p>A feature subscribes without knowing the wire format:
 *
 * <pre>
 * addon.payloads().subscribe(AccountBalancePayload.class, payload -&gt; {
 *   double balance = payload.balance();
 * });
 * </pre>
 *
 * The same object is also published as {@link ClientPayloadEvent}. Channels and codecs that
 * show up later can be added with {@link #registerChannel}, {@link #registerGrieferGamesCodec}
 * and {@link #registerMysteryModCodec} during addon startup, before the client joins.
 */
public class PayloadReceiver {

  private final GrieferGames griefergames;
  private final GrieferGamesPayloadChannel grieferGamesChannel = new GrieferGamesPayloadChannel();
  private final MysteryModPayloadChannel mysteryModChannel = new MysteryModPayloadChannel();
  private final List<IncomingPayloadChannel> channels = new CopyOnWriteArrayList<>();
  private final List<Subscription> subscriptions = new CopyOnWriteArrayList<>();
  private final Set<String> loggedUnknownIds = ConcurrentHashMap.newKeySet();

  public PayloadReceiver(GrieferGames griefergames) {
    this.griefergames = griefergames;
    registerChannel(grieferGamesChannel);
    registerChannel(mysteryModChannel);
  }

  public void registerChannel(IncomingPayloadChannel channel) {
    channels.add(channel);
    Laby.references().payloadRegistry().registerPayloadChannel(channel.identifier());
  }

  public void registerGrieferGamesCodec(PayloadCodec<?> codec) {
    grieferGamesChannel.register(codec);
  }

  public void registerMysteryModCodec(String key, JsonPayloadDecoder decoder) {
    mysteryModChannel.register(key, decoder);
  }

  public <T extends ClientPayload> void subscribe(Class<T> type, Consumer<T> handler) {
    subscriptions.add(new Subscription(type, handler));
  }

  @Subscribe
  public void onNetworkPayload(NetworkPayloadEvent event) {
    if (event.side() != Side.RECEIVE || !griefergames.state().isOnGrieferGames()) {
      return;
    }
    ResourceLocation identifier = event.identifier();
    if (identifier == null) {
      return;
    }
    for (IncomingPayloadChannel channel : channels) {
      if (!sameChannel(channel.identifier(), identifier)) {
        continue;
      }
      dispatch(channel, event.getPayload());
      return;
    }
  }

  private void dispatch(IncomingPayloadChannel channel, byte[] raw) {
    Optional<ClientPayload> decoded;
    try {
      decoded = channel.decode(raw);
    } catch (IOException | RuntimeException exception) {
      griefergames.logger().warn(
          GrieferGames.LOG_PREFIX + "Could not decode payload on "
              + channel.identifier().getNamespace() + ":" + channel.identifier().getPath(),
          exception
      );
      return;
    }
    if (decoded.isEmpty()) {
      return;
    }
    ClientPayload payload = decoded.get();
    logUnknownOnce(channel, payload);
    deliver(payload);
  }

  /**
   * Passes a payload to subscribers without a network packet.
   * The dev command uses this to simulate a balance update.
   */
  public void dispatchLocal(ClientPayload payload) {
    if (payload == null) {
      return;
    }
    deliver(payload);
  }

  private void deliver(ClientPayload payload) {
    for (Subscription subscription : subscriptions) {
      if (!subscription.type.isInstance(payload)) {
        continue;
      }
      try {
        subscription.handler.accept(payload);
      } catch (RuntimeException exception) {
        griefergames.logger().warn(
            GrieferGames.LOG_PREFIX + "Payload subscriber failed for " + payload.id(),
            exception
        );
      }
    }
    Laby.fireEvent(new ClientPayloadEvent(payload));
  }

  private void logUnknownOnce(IncomingPayloadChannel channel, ClientPayload payload) {
    if (!(payload instanceof UnknownPayload) && !(payload instanceof MysteryModMessage)) {
      return;
    }
    String key = channel.identifier().getNamespace() + ":" + payload.id();
    if (loggedUnknownIds.add(key)) {
      griefergames.logger().info(
          GrieferGames.LOG_PREFIX + "No codec for payload " + key
              + ". Subscribe to UnknownPayload or MysteryModMessage, or register a codec."
      );
    }
  }

  private static boolean sameChannel(ResourceLocation registered, ResourceLocation incoming) {
    return registered.getNamespace().equals(incoming.getNamespace())
        && registered.getPath().equals(incoming.getPath());
  }

  private static final class Subscription {
    private final Class<?> type;
    private final Consumer<ClientPayload> handler;

    private <T extends ClientPayload> Subscription(Class<T> type, Consumer<T> handler) {
      this.type = type;
      this.handler = payload -> handler.accept(type.cast(payload));
    }
  }
}
