package de.cosmohdx.griefergames.payload.model;

import de.cosmohdx.griefergames.payload.ClientPayload;
import java.util.Arrays;

/**
 * A {@code griefergames:main} id that has no codec yet. {@code data} is everything after the id,
 * still in {@link java.io.DataOutputStream} layout.
 */
public record UnknownPayload(String id, byte[] data) implements ClientPayload {

  public UnknownPayload {
    data = data == null ? new byte[0] : data.clone();
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof UnknownPayload payload)) {
      return false;
    }
    return id.equals(payload.id) && Arrays.equals(data, payload.data);
  }

  @Override
  public int hashCode() {
    return 31 * id.hashCode() + Arrays.hashCode(data);
  }

  @Override
  public String toString() {
    return "UnknownPayload[id=" + id + ", dataLength=" + data.length + "]";
  }
}
