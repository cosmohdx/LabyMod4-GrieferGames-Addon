package de.cosmohdx.griefergames.feature.afk;

import de.cosmohdx.griefergames.GrieferGames;
import de.cosmohdx.griefergames.core.GrieferGamesConfig;
import net.labymod.api.Laby;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.NamedTextColor;
import net.labymod.api.util.I18n;

public class AfkActions {

  private final GrieferGames griefergames;

  public AfkActions(GrieferGames griefergames) {
    this.griefergames = griefergames;
  }

  /**
   * Performs all actions to do if the player goes afk
   * @param afk true if the player goes afk
   */
  public void perform(boolean afk) {
    if (afk) {
      griefergames.displayAddonMessage(Component.text(I18n.translate(griefergames.namespace() + ".messages.afkMessage"), NamedTextColor.GRAY));

      if (griefergames.configuration().automations().afkConfig().afkNick().get() && griefergames.helper().isCityBuild(griefergames.state().getSubServer())) {
        String nickname = griefergames.configuration().automations().afkConfig().afkNickname().get();
        if (nickname.isBlank()) {
          nickname = GrieferGamesConfig.DEFAULT_AFK_NICKNAME;
        }
        nickname = nickname.replace("%name%", Laby.labyAPI().getName());
        if (nickname.length() > 16) {
          nickname = nickname.substring(0, 16);
        }

        griefergames.sendMessage("/nick " + nickname);
      }
    } else {
      griefergames.displayAddonMessage(Component.text(I18n.translate(griefergames.namespace() + ".messages.afkBackMessage"), NamedTextColor.GRAY));
      if (griefergames.configuration().automations().afkConfig().afkNick().get() && griefergames.helper().isCityBuild(griefergames.state().getSubServer())) {
        griefergames.sendMessage("/unnick");
      }
    }
  }
}
