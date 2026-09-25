package de.cosmohdx.griefergames.feature.chat;

import de.cosmohdx.griefergames.GrieferGames;
import de.cosmohdx.griefergames.feature.chat.GGChatProcessEvent;
import de.cosmohdx.griefergames.core.SubServerType;
import net.labymod.api.event.Subscribe;

public class Vote extends ChatModule {
  private final GrieferGames griefergames;

  public Vote(GrieferGames griefergames) {
    this.griefergames = griefergames;
  }

  @Subscribe
  public void messageProcessEvent(GGChatProcessEvent event) {
    if(event.isCancelled()) return;
    if(!griefergames.isSubServerType(SubServerType.REGULAR)) return;
    if (!griefergames.configuration().chatConfig().isHideVoteMessages()) return;

    String plain = event.getMessage().getPlainText();
    if (plain.startsWith("[GrieferGames]") &&
      (plain.endsWith("hat für unseren Server gevotet! /vote") || plain.endsWith("hat gevotet und erhält ein tolles Geschenk! /vote"))) {
      event.setCancelled(true);
    }
  }
}
