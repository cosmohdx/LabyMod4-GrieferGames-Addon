package de.neocraftr.griefergames.chat.modules;

import de.neocraftr.griefergames.GrieferGames;
import de.neocraftr.griefergames.chat.events.GGChatProcessEvent;
import de.neocraftr.griefergames.enums.SubServerType;
import de.neocraftr.griefergames.settings.GrieferGamesConfig;
import net.labymod.api.client.component.Component;
import net.labymod.api.event.Subscribe;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

//@Deprecated(since = "1.1.1", forRemoval = true)
public class ChatTime extends ChatModule {
  private final GrieferGames griefergames;
  private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

  public ChatTime(GrieferGames griefergames) {
    this.griefergames = griefergames;
  }

  @Subscribe
  public void messageProcessEvent(GGChatProcessEvent event) {
    if (event.isCancelled()) return;
    if (!griefergames.configuration().chatConfig().isShowChatTime()) return;
    if (event.getMessage().getPlainText().isBlank()) return;

    String[] time = LocalDateTime.now().format(formatter).split(":");
    String timeMsg = griefergames.configuration().chatConfig().chatTimeFormat().get();
    if (timeMsg.isBlank()) {
      timeMsg = GrieferGamesConfig.DEFAULT_CHATTIME_FORMAT;
    }
    timeMsg = timeMsg.replace("&", "§");
    timeMsg = timeMsg.replace("{h}", time[0]);
    timeMsg = timeMsg.replace("{m}", time[1]);
    timeMsg = timeMsg.replace("{s}", time[2]);

    if (griefergames.configuration().chatConfig().chatTimeAfterMessage().get()) {
      event.getMessage().component().append(Component.text("§r " + timeMsg));
    } else {
      List<Component> children = new ArrayList<>(event.getMessage().component().getChildren());
      children.add(0, Component.text(timeMsg + " "));
      event.getMessage().component().setChildren(children);
    }
  }
}
