package de.cosmohdx.griefergames.feature.chat;

import de.cosmohdx.griefergames.GrieferGames;
import de.cosmohdx.griefergames.feature.chat.GGChatProcessEvent;
import de.cosmohdx.griefergames.core.SubServerType;
import net.labymod.api.event.Subscribe;

public class PlotChat extends ChatModule {
  private final GrieferGames griefergames;

  public PlotChat(GrieferGames griefergames) {
    this.griefergames = griefergames;
  }

  @Subscribe
  public void messageProcessEvent(GGChatProcessEvent event) {
    if(event.isCancelled()) return;
    if (!griefergames.configuration().chat().routePlotChat()) return;
    if (event.getMessage().getPlainText().startsWith("[Plot-Chat]")) {
      event.setSecondChat(true);
    }
  }
}
