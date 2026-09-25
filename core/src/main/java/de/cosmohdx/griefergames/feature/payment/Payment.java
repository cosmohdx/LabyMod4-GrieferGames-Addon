package de.cosmohdx.griefergames.feature.payment;

import de.cosmohdx.griefergames.GrieferGames;
import de.cosmohdx.griefergames.feature.chat.ChatModule;
import de.cosmohdx.griefergames.feature.chat.GGChatProcessEvent;
import de.cosmohdx.griefergames.core.SubServerType;
import de.cosmohdx.griefergames.feature.payment.TransactionType;
import net.labymod.api.Laby;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.event.HoverEvent;
import net.labymod.api.client.component.format.NamedTextColor;
import net.labymod.api.client.component.format.Style;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.resources.ResourceLocation;
import net.labymod.api.event.Subscribe;
import net.labymod.api.notification.Notification;
import net.labymod.api.util.I18n;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Payment extends ChatModule {
  private final GrieferGames griefergames;
  private final Pattern receiveMoneyRegex = Pattern.compile("^([A-Za-z\\-\\+]+) \\u2503 (~?\\!?\\w{1,16}) hat dir \\$((?:[1-9]\\d{0,2}(?:,\\d{1,3})*|0)(?:\\.\\d+)?) gegeben\\.$");
  private final Pattern payMoneyRegex = Pattern.compile("^Du hast ([A-Za-z\\-\\+]+) \\u2503 (~?\\!?\\w{1,16}) \\$((?:[1-9]\\d{0,2}(?:,\\d{1,3})*|0)(?:\\.\\d+)?) gegeben\\.$");
  private final Pattern earnMoneyRegex = Pattern.compile("\\$((?:[1-9]\\d{0,2}(?:,\\d{1,3})*|0)(?:\\.\\d+)?) wurde zu deinem Konto hinzugefügt\\.$");
  private final DecimalFormat moneyFormat = (DecimalFormat) DecimalFormat.getNumberInstance(Locale.ENGLISH);

  public Payment(GrieferGames griefergames) {
    this.griefergames = griefergames;
  }

  @Subscribe
  public void messageProcessEvent(GGChatProcessEvent event) {
    if (event.getMessage() == null) {
      return;
    }
    String plain = event.getMessage().getPlainText();
    if (plain == null || plain.isBlank()) {
      return;
    }
    if (griefergames.configuration().payment().logBalanceTiming()) {
      logPaymentTiming(plain);
    }
    if (!griefergames.configuration().payment().isEnabled()) {
      return;
    }
    if (griefergames.state().getSubServerType() == SubServerType.REGULAR) {

      Matcher receiveMoneyMatcher = receiveMoneyRegex.matcher(plain);
      if (receiveMoneyMatcher.find()) {
        String rank = receiveMoneyMatcher.group(1);
        String name = receiveMoneyMatcher.group(2);
        double amount = getAmount(receiveMoneyMatcher.group(3));

        if (!event.getMessage().getFormattedText().contains("§f §ahat dir $")) {
          griefergames.state().addIncome(amount);

          if (griefergames.configuration().payment().logTransactions()) {
            griefergames.fileManager().logTransaction(rank + " ┃ " + name, amount, TransactionType.RECEIVE);
          }

          if (griefergames.configuration().payment().paymentNotification()) {
            sendPaymentNotification(TransactionType.RECEIVE, rank, name, amount);
          }

          if (griefergames.configuration().payment().highlightPayments()) {
            event.getMessage().component().append(Component.text(" \u2714", Style.builder()
              .color(NamedTextColor.GREEN)
              .hoverEvent(HoverEvent.showText(Component.text(I18n.translate(griefergames.namespace() + ".messages.verifiedPayment"), NamedTextColor.GREEN)))
              .build()));
          }
        } else {
          if (griefergames.configuration().payment().fakeMoneyWarning()) {
            String warningMessage = "§e§l" + I18n.translate(griefergames.namespace() + ".messages.warning") + " §c"
              + I18n.translate(griefergames.namespace() + ".messages.fakeMoney")
              .replace("{player}", "§e" + rank + " ┃ " + name + "§c")
              .replace("{amount}", "§e$" + receiveMoneyMatcher.group(3) + "§c");

            griefergames.displayMessage("\n\n");
            griefergames.displayAddonMessage(warningMessage);
          }
        }
      }

      Matcher payMoneyMatcher = payMoneyRegex.matcher(plain);
      if (payMoneyMatcher.find()) {
        String rank = payMoneyMatcher.group(1);
        String name = payMoneyMatcher.group(2);
        double amount = getAmount(payMoneyMatcher.group(3));

        griefergames.state().addIncome(amount * -1);

        if (griefergames.configuration().payment().logTransactions()) {
          griefergames.fileManager().logTransaction(rank + " ┃ " + name, amount, TransactionType.PAY);
        }
        if (griefergames.configuration().payment().paymentNotification()) {
          sendPaymentNotification(TransactionType.PAY, rank, name, amount);
        }
      }

      Matcher earnMoneyMatcher = earnMoneyRegex.matcher(plain);
      if (earnMoneyMatcher.find()) {
        double amount = getAmount(earnMoneyMatcher.group(1));

        griefergames.state().addIncome(amount);

        if (griefergames.configuration().payment().logTransactions()) {
          griefergames.fileManager().logTransaction(null, amount, TransactionType.MONEYDROP);
        }
        if (griefergames.configuration().payment().paymentNotification()) {
          sendPaymentNotification(TransactionType.MONEYDROP, amount);
        }
      }

    }
  }

  private void logPaymentTiming(String plain) {
    String type = null;
    if (receiveMoneyRegex.matcher(plain).find()) {
      type = "receive";
    } else if (payMoneyRegex.matcher(plain).find()) {
      type = "pay";
    } else if (earnMoneyRegex.matcher(plain).find()) {
      type = "moneydrop";
    }
    if (type == null) {
      return;
    }
    griefergames.logger().info(
        GrieferGames.LOG_PREFIX + "payment t=" + System.currentTimeMillis()
            + " n=" + System.nanoTime()
            + " type=" + type
            + " text=" + plain
    );
  }

  public double getAmount(String message) {
    message = message.replaceAll(",", "");
    try {
      return Double.parseDouble(message);
    } catch (NumberFormatException e) {
      e.printStackTrace();
      return -1;
    }
  }

  public void sendPaymentNotification(TransactionType type, String rank, String name, double amount) {
    String message = I18n.translate(griefergames.namespace()+".notifications.payment."+type.name().toLowerCase());
    message = message.replace("{amount}", "$"+moneyFormat.format(amount)).replace("{player}", rank+" | "+name);

    Laby.labyAPI().notificationController().push(Notification.builder()
        .title(Component.text(I18n.translate(griefergames.namespace()+".notifications.payment.title"), NamedTextColor.GREEN))
        .text(Component.text(message))
        .icon(Icon.head(name)).build());
  }

  public void sendPaymentNotification(TransactionType type, double amount) {
    String message = I18n.translate(griefergames.namespace()+".notifications.payment."+type.name().toLowerCase());
    message = message.replace("{amount}", "$"+moneyFormat.format(amount));

    Laby.labyAPI().notificationController().push(Notification.builder()
        .title(Component.text(I18n.translate(griefergames.namespace()+".notifications.payment.title"), NamedTextColor.GREEN))
        .text(Component.text(message))
        .icon(Icon.texture(ResourceLocation.create(griefergames.namespace(), "textures/cash.png"))).build());
  }
}
