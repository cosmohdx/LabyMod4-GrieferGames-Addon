package de.cosmohdx.griefergames.payload.model;

import com.google.gson.JsonElement;
import de.cosmohdx.griefergames.payload.ClientPayload;
import java.io.IOException;
import java.math.BigDecimal;

/**
 * MysteryMod key {@code bank}. {@code amount} is the bank balance in the same units as
 * {@link BankBalancePayload}, including fractional currency when the number has a fraction.
 */
public record MysteryModBankPayload(BigDecimal amount) implements ClientPayload {

  public static final String ID = "bank";

  public MysteryModBankPayload {
    if (amount == null) {
      throw new IllegalArgumentException("amount is required");
    }
  }

  @Override
  public String id() {
    return ID;
  }

  public static MysteryModBankPayload decode(JsonElement body) throws IOException {
    if (body == null || !body.isJsonObject()) {
      throw new IOException("bank payload is not a JSON object");
    }
    JsonElement amount = body.getAsJsonObject().get("amount");
    if (amount == null || amount.isJsonNull() || !amount.isJsonPrimitive()
        || !amount.getAsJsonPrimitive().isNumber()) {
      throw new IOException("bank payload has no amount");
    }
    try {
      return new MysteryModBankPayload(amount.getAsBigDecimal());
    } catch (NumberFormatException exception) {
      throw new IOException("bank payload amount is not a number", exception);
    }
  }
}
