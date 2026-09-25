package de.cosmohdx.griefergames.payload.model;

import de.cosmohdx.griefergames.payload.ClientPayload;
import java.io.DataInput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Currently active boosters. Each entry is a type name plus a multiplier (legacy tier,
 * cloud active multiplier). The payload does not include a remaining duration.
 */
public record BoosterPayload(List<Entry> boosters) implements ClientPayload {

  public static final String ID = "booster";
  public static final int MAX_ENTRIES = 64;

  public BoosterPayload {
    boosters = List.copyOf(boosters);
  }

  @Override
  public String id() {
    return ID;
  }

  public static BoosterPayload decode(DataInput input) throws IOException {
    int count = input.readInt();
    if (count < 0 || count > MAX_ENTRIES) {
      throw new IOException("Unexpected booster count: " + count);
    }
    List<Entry> entries = new ArrayList<>(count);
    for (int i = 0; i < count; i++) {
      entries.add(new Entry(input.readUTF(), input.readInt()));
    }
    return new BoosterPayload(entries);
  }

  public record Entry(String type, int multiplier) {
  }
}
