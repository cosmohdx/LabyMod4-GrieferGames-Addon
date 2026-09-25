package de.cosmohdx.griefergames.payload;

import net.labymod.api.event.Event;

/**
 * Fired after a payload was decoded. Prefer {@link PayloadReceiver#subscribe}
 * when the handler should not also be a LabyMod listener.
 */
public class ClientPayloadEvent implements Event {

  private final ClientPayload payload;

  public ClientPayloadEvent(ClientPayload payload) {
    this.payload = payload;
  }

  public ClientPayload payload() {
    return payload;
  }
}
