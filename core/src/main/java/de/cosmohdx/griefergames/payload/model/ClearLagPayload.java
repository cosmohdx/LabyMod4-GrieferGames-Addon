package de.cosmohdx.griefergames.payload.model;

import de.cosmohdx.griefergames.payload.ClientPayload;
import java.io.DataInput;
import java.io.IOException;

/**
 * Seconds until the next item clear. {@code -1} means the server could not read the timer.
 * Sent on join and about every 10 seconds.
 */
public record ClearLagPayload(long remainingSeconds) implements ClientPayload {

  public static final String ID = "clearlag";

  @Override
  public String id() {
    return ID;
  }

  public boolean known() {
    return remainingSeconds >= 0;
  }

  public static ClearLagPayload decode(DataInput input) throws IOException {
    return new ClearLagPayload(input.readLong());
  }
}
