package de.cosmohdx.griefergames.payload.model;

import de.cosmohdx.griefergames.payload.ClientPayload;
import java.io.DataInput;
import java.io.IOException;

/**
 * Seconds until the next entity clear. {@code -1} means the server could not read the timer.
 * Sent on join and about every 10 seconds. On cloud this is the entity-clear task;
 * item clear is {@link ClearLagPayload}.
 */
public record EntityRemoverPayload(long remainingSeconds) implements ClientPayload {

  public static final String ID = "entityremover";

  @Override
  public String id() {
    return ID;
  }

  public boolean known() {
    return remainingSeconds >= 0;
  }

  public static EntityRemoverPayload decode(DataInput input) throws IOException {
    return new EntityRemoverPayload(input.readLong());
  }
}
