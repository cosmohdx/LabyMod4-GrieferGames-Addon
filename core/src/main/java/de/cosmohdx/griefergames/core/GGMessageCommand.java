package de.cosmohdx.griefergames.core;

import de.cosmohdx.griefergames.GrieferGames;
import de.cosmohdx.griefergames.payload.model.AccountBalancePayload;
import de.cosmohdx.griefergames.payload.model.BankBalancePayload;
import java.math.BigDecimal;
import java.math.RoundingMode;
import net.labymod.api.client.chat.command.Command;
import net.labymod.api.client.chat.command.SubCommand;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.NamedTextColor;
import net.labymod.api.client.component.serializer.gson.GsonComponentSerializer;

public class GGMessageCommand extends Command {
  private final GrieferGames griefergames;

  public GGMessageCommand(GrieferGames griefergames) {
    super("ggmessage");
    this.griefergames = griefergames;

    this.withSubCommand(new SubCommand("text") {
      @Override
      public boolean execute(String prefix, String[] arguments) {
        if (arguments.length > 0) {
          String message = String.join(" ", arguments).replace("&", "§");
          griefergames.displayMessage(Component.text(message));
        } else {
          usage("text <colored_message>");
        }
        return true;
      }
    });

    this.withSubCommand(new SubCommand("json") {
      @Override
      public boolean execute(String prefix, String[] arguments) {
        if (arguments.length > 0) {
          String message = String.join(" ", arguments);
          try {
            griefergames.displayMessage(GsonComponentSerializer.gson().deserialize(message));
          } catch (Exception exception) {
            exception.printStackTrace();
            griefergames.displayAddonMessage(Component.text("Invalide json.", NamedTextColor.RED));
          }
        } else {
          usage("json <json_message>");
        }
        return true;
      }
    });

    this.withSubCommand(new SubCommand("balance") {
      @Override
      public boolean execute(String prefix, String[] arguments) {
        injectBalance(false, arguments);
        return true;
      }
    });

    this.withSubCommand(new SubCommand("bank") {
      @Override
      public boolean execute(String prefix, String[] arguments) {
        injectBalance(true, arguments);
        return true;
      }
    });
  }

  @Override
  public boolean execute(String prefix, String[] arguments) {
    usage("text <colored_message>");
    usage("json <json_message>");
    usage("balance <amount>");
    usage("bank <amount>");
    return true;
  }

  static BigDecimal parseAmount(String raw) {
    if (raw == null) {
      return null;
    }
    String text = raw.trim().replace("$", "").replace(" ", "");
    if (text.isEmpty()) {
      return null;
    }
    boolean negative = text.startsWith("-");
    if (negative) {
      text = text.substring(1);
    }
    if (text.isEmpty()) {
      return null;
    }
    int lastComma = text.lastIndexOf(',');
    int lastDot = text.lastIndexOf('.');
    if (lastComma >= 0 && lastDot >= 0) {
      if (lastComma > lastDot) {
        text = text.replace(".", "").replace(',', '.');
      } else {
        text = text.replace(",", "");
      }
    } else if (lastComma >= 0) {
      int commas = 0;
      for (int index = 0; index < text.length(); index++) {
        if (text.charAt(index) == ',') {
          commas++;
        }
      }
      if (commas > 1 || text.length() - lastComma - 1 == 3) {
        text = text.replace(",", "");
      } else {
        text = text.replace(',', '.');
      }
    }
    try {
      BigDecimal value = new BigDecimal(text);
      return negative ? value.negate() : value;
    } catch (NumberFormatException exception) {
      return null;
    }
  }

  private void injectBalance(boolean bank, String[] arguments) {
    String label = bank ? "bank" : "balance";
    if (arguments.length == 0) {
      usage(label + " <amount>");
      return;
    }
    BigDecimal amount = parseAmount(String.join(" ", arguments));
    if (amount == null) {
      usage(label + " <amount>");
      return;
    }
    BigDecimal scaled = amount.setScale(2, RoundingMode.HALF_UP);
    if (bank) {
      griefergames.payloads().dispatchLocal(new BankBalancePayload(scaled.doubleValue()));
    } else {
      griefergames.payloads().dispatchLocal(new AccountBalancePayload(scaled.doubleValue()));
    }
    griefergames.displayAddonMessage(Component.text(
        (bank ? "Bank" : "Balance") + " set to $" + scaled.toPlainString(),
        NamedTextColor.GRAY
    ));
  }

  private void usage(String syntax) {
    griefergames.displayAddonMessage(Component.text(
        "Usage: /ggmessage " + syntax,
        NamedTextColor.RED
    ));
  }
}
