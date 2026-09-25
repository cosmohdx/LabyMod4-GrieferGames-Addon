package de.cosmohdx.griefergames.feature.chat;

import de.cosmohdx.griefergames.GrieferGames;
import de.cosmohdx.griefergames.feature.chat.GGChatProcessEvent;
import de.cosmohdx.griefergames.core.SubServerType;
import net.labymod.api.Laby;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.event.HoverEvent;
import net.labymod.api.client.component.format.NamedTextColor;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.resources.ResourceLocation;
import net.labymod.api.event.Subscribe;
import net.labymod.api.notification.Notification;
import net.labymod.api.util.I18n;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItemRemover extends ChatModule {
  private final GrieferGames griefergames;
  private final Pattern itemRemoverMessageRegex = Pattern.compile("^\\[GrieferGames\\] Warnung! Die auf dem Boden liegenden Items werden in ([0-9]+) Sekunden entfernt!$");
  private final Pattern itemRemoverDoneMessageRegex = Pattern.compile("^\\[GrieferGames\\] Es wurden ([0-9]+) auf dem Boden liegende Items entfernt!$");
  private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

  public ItemRemover(GrieferGames griefergames) {
    this.griefergames = griefergames;
  }

  @Subscribe
  public void messageProcessEvent(GGChatProcessEvent event) {
    if (event.isCancelled()) return;
    if (griefergames.state().getSubServerType() == SubServerType.REGULAR) {
      String plain = event.getMessage().getPlainText();
      if (plain.isBlank()) return;

      Matcher itemRemoverMessage = itemRemoverMessageRegex.matcher(plain);
      Matcher itemRemoverDoneMessage = itemRemoverDoneMessageRegex.matcher(plain);

      boolean done;
      if ((done = itemRemoverDoneMessage.find()) || itemRemoverMessage.find()) {
        if (griefergames.configuration().chatConfig().isRemoverLastTimeHover() && done) {
          String dateNowStr = LocalDateTime.now().format(formatter);
          Component hoverText = Component.text(dateNowStr);
          event.getMessage().component().style(event.getMessage().component().style().hoverEvent(HoverEvent.showText(hoverText)));
        }

        if (griefergames.configuration().chatConfig().isRemoverChatRight()) {
          event.setSecondChat(true);
        }

        if (griefergames.configuration().chatConfig().isRemoverNotification() && !done) {
          Laby.labyAPI().notificationController().push(Notification.builder()
            .title(Component.text("ItemRemover", NamedTextColor.RED))
            .text(Component.text(I18n.translate(griefergames.namespace() + ".notifications.itemRemover").replace("{time}", itemRemoverMessage.group(1))))
            .icon(Icon.texture(ResourceLocation.create(griefergames.namespace(), "textures/itemremover.png")))
            .build());
        }
      }
    }
  }
}
