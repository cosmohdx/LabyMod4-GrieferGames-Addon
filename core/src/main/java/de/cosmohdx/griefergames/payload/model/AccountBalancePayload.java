package de.cosmohdx.griefergames.payload.model;

import de.cosmohdx.griefergames.payload.ClientPayload;
import java.io.DataInput;
import java.io.IOException;

/** Cash account balance. Sent on join and whenever the balance changes. */
public record AccountBalancePayload(double balance) implements ClientPayload {

  public static final String ID = "accountbalance";

  @Override
  public String id() {
    return ID;
  }

  public static AccountBalancePayload decode(DataInput input) throws IOException {
    return new AccountBalancePayload(input.readDouble());
  }
}
