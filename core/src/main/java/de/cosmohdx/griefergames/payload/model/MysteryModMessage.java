package de.cosmohdx.griefergames.payload.model;

import de.cosmohdx.griefergames.payload.ClientPayload;

/**
 * MysteryMod message whose key has no dedicated codec. {@code json} is the raw second string.
 */
public record MysteryModMessage(String key, String json) implements ClientPayload {

  public MysteryModMessage {
    if (key == null || json == null) {
      throw new IllegalArgumentException("key and json are required");
    }
  }

  @Override
  public String id() {
    return key;
  }
}
