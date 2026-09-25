package de.cosmohdx.griefergames.feature.chat;

import de.cosmohdx.griefergames.GrieferGames;
import de.cosmohdx.griefergames.feature.chat.GGChatProcessEvent;
import de.cosmohdx.griefergames.core.SubServerType;
import de.cosmohdx.griefergames.core.GrieferGamesConfig;
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
    if (!griefergames.configuration().chat().showChatTime()) return;
    if (event.getMessage().getPlainText().isBlank()) return;

    String[] time = LocalDateTime.now().format(formatter).split(":");
    String timeMsg = griefergames.configuration().chat().chatTimeFormat();
    if (timeMsg.isBlank()) {
      timeMsg = GrieferGamesConfig.DEFAULT_CHATTIME_FORMAT;
    }
    timeMsg = timeMsg.replace("&", "§");
    timeMsg = timeMsg.replace("{h}", time[0]);
    timeMsg = timeMsg.replace("{m}", time[1]);
    timeMsg = timeMsg.replace("{s}", time[2]);

    if (griefergames.configuration().chat().chatTimeAfterMessage()) {
      event.getMessage().component().append(Component.text("§r " + timeMsg));
    } else {
      List<Component> children = new ArrayList<>(event.getMessage().component().getChildren());
      children.add(0, Component.text(timeMsg + " "));
      event.getMessage().component().setChildren(children);
    }
  }
}
