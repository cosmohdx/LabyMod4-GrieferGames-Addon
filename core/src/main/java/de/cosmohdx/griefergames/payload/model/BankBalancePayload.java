package de.cosmohdx.griefergames.payload.model;

import de.cosmohdx.griefergames.payload.ClientPayload;
import java.io.DataInput;
import java.io.IOException;

/** Bank account balance. Sent on join and whenever the balance changes. */
public record BankBalancePayload(double balance) implements ClientPayload {

  public static final String ID = "bankbalance";

  @Override
  public String id() {
    return ID;
  }

  public static BankBalancePayload decode(DataInput input) throws IOException {
    return new BankBalancePayload(input.readDouble());
  }
}
