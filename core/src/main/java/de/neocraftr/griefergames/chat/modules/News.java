package de.neocraftr.griefergames.chat.modules;

import de.neocraftr.griefergames.GrieferGames;
import de.neocraftr.griefergames.chat.events.GGChatProcessEvent;
import de.neocraftr.griefergames.enums.SubServerType;
import net.labymod.api.event.Subscribe;

public class News extends ChatModule {
  private final GrieferGames griefergames;
  private boolean isNewsMessage = false;

  public News(GrieferGames griefergames) {
    this.griefergames = griefergames;
  }

  @Subscribe
  public void messageProcessEvent(GGChatProcessEvent event) {
    if(event.isCancelled()) return;
    if (!griefergames.configuration().chatConfig().isHideNewsMessages()) return;

    if (event.getMessage().getFormattedText().contains("§f§m------------§8 [ §6News§8 ] §f§m------------\n\n"))
      event.setCancelled(true);
  }
}
