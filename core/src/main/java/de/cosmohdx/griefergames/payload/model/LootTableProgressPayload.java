package de.cosmohdx.griefergames.payload.model;

import de.cosmohdx.griefergames.payload.ClientPayload;
import java.io.DataInput;
import java.io.IOException;

/**
 * Pulse sent when the player collects one more block-of-the-day item.
 * The body is empty: the server does not include the new count.
 * The Kotlin class is named {@code LootTableProgressPayload}, the wire id is not.
 */
public record LootTableProgressPayload() implements ClientPayload {

  public static final String ID = "blockoftheday_progress";

  @Override
  public String id() {
    return ID;
  }

  public static LootTableProgressPayload decode(DataInput input) throws IOException {
    return new LootTableProgressPayload();
  }
}
