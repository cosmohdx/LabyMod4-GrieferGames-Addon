package de.cosmohdx.griefergames.payload.model;

import de.cosmohdx.griefergames.payload.ClientPayload;
import java.io.DataInput;
import java.io.IOException;

/**
 * Today's loot target. Legacy sends {@code type} {@code BLOCK} or {@code ENTITY}.
 * Cloud sends {@code MATERIAL} or {@code ENTITY}. Unused strings are empty and
 * {@code blockData} is {@code 0} on cloud.
 */
public record BlockOfTheDayPayload(
    String type,
    String blockMaterial,
    int blockData,
    String entityType
) implements ClientPayload {

  public static final String ID = "blockoftheday";

  @Override
  public String id() {
    return ID;
  }

  public boolean entity() {
    return "ENTITY".equalsIgnoreCase(type);
  }

  public static BlockOfTheDayPayload decode(DataInput input) throws IOException {
    return new BlockOfTheDayPayload(
        input.readUTF(),
        input.readUTF(),
        input.readInt(),
        input.readUTF()
    );
  }
}
