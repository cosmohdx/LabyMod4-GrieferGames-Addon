package de.cosmohdx.griefergames.payload.model;

import de.cosmohdx.griefergames.payload.ClientPayload;
import java.io.DataInput;
import java.io.IOException;

/**
 * Whether the player's plot-chat attribute is enabled. Sent on join and shortly after
 * {@code /plot chat}. This is not a chat message.
 */
public record PlotChatConfigurationPayload(boolean enabled) implements ClientPayload {

  public static final String ID = "plotchat_configuration";

  @Override
  public String id() {
    return ID;
  }

  public static PlotChatConfigurationPayload decode(DataInput input) throws IOException {
    return new PlotChatConfigurationPayload(input.readBoolean());
  }
}
