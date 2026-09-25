package de.cosmohdx.griefergames.feature.payment;

import de.cosmohdx.griefergames.GrieferGames;
import de.cosmohdx.griefergames.feature.chat.ChatModule;
import de.cosmohdx.griefergames.feature.chat.GGChatProcessEvent;
import de.cosmohdx.griefergames.core.SubServerType;
import net.labymod.api.Laby;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.NamedTextColor;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.resources.ResourceLocation;
import net.labymod.api.event.Subscribe;
import net.labymod.api.notification.Notification;
import net.labymod.api.util.I18n;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Bank extends ChatModule {
  private final GrieferGames griefergames;
  private final Pattern moneyBankRegexp = Pattern.compile("\\s(\\d+)");
  private final DecimalFormat moneyFormat = (DecimalFormat) DecimalFormat.getNumberInstance(Locale.ENGLISH);

  public Bank(GrieferGames griefergames) {
    this.griefergames = griefergames;
  }

  @Subscribe
  public void messageProcessEvent(GGChatProcessEvent event) {
    if(event.isCancelled()) return;
    if (!griefergames.configuration().payment().isEnabled()) return;
    if (griefergames.state().getSubServerType() == SubServerType.REGULAR) {
      String plain = event.getMessage().getPlainText();

      if (plain.startsWith("[Bank] ")) {
        if (griefergames.configuration().payment().bankChatRight().get()) {
          event.setSecondChat(true);
        }

        boolean deposit;
        if ((deposit = plain.endsWith("auf dein Bankkonto eingezahlt.")) || plain.endsWith("von deinem Bankkonto abgehoben.")) {
          if (griefergames.configuration().payment().bankAchievement().get()) {
            String message = I18n.translate(griefergames.namespace() + ".notifications.bank." + (deposit ? "deposit" : "withdraw"));
            message = message.replace("{amount}", "$" + moneyFormat.format(getMoneyBank(plain)));

            Laby.labyAPI().notificationController().push(Notification.builder()
                    .title(Component.text(I18n.translate(griefergames.namespace() + ".notifications.bank.title"), NamedTextColor.DARK_GREEN))
                    .text(Component.text(message))
                    .icon(Icon.texture(ResourceLocation.create(griefergames.namespace(), "textures/bank.png"))).build());
          }
        }
      }
    }
  }

  private int getMoneyBank(String message) {
    Matcher matcher = moneyBankRegexp.matcher(message);
    if (matcher.find()) {
      String moneyStr = matcher.group(1).trim();
      if (moneyStr.length() > 0) {
        try {
          return Integer.parseInt(moneyStr);
        } catch (NumberFormatException ignored) {}
      }
    }
    return -1;
  }
}
