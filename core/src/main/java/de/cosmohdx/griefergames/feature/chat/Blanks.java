package de.cosmohdx.griefergames.feature.chat;

import de.cosmohdx.griefergames.GrieferGames;
import de.cosmohdx.griefergames.feature.chat.GGChatProcessEvent;
import de.cosmohdx.griefergames.core.SubServerType;
import net.labymod.api.event.Subscribe;

public class Blanks extends ChatModule {
  private final GrieferGames griefergames;

  public Blanks(GrieferGames griefergames) {
    this.griefergames = griefergames;
  }

  @Subscribe
  public void messageProcessEvent(GGChatProcessEvent event) {
    if (griefergames.configuration().chat().hideBlankLines()) {
      if (event.getMessage().getPlainText().isBlank()) {
        event.setCancelled(true);
      }
      if (event.getMessage().getFormattedText().equals("&7")) {
        event.setCancelled(true);
      }
    }
    if (griefergames.configuration().chat().hideSupremeBlankLines()) {
      if (event.getMessage().getPlainText().trim().equals("\u00BB")) {
        event.setCancelled(true);
      }
    }
  }
}