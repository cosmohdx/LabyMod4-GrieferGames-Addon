package de.cosmohdx.griefergames.feature.chat;

import de.cosmohdx.griefergames.GrieferGames;
import de.cosmohdx.griefergames.feature.chat.GGChatProcessEvent;
import de.cosmohdx.griefergames.core.SubServerType;
import net.labymod.api.event.Subscribe;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Nickname {
  private final GrieferGames griefergames;
  private final Pattern nicknameMsgRegex = Pattern.compile("^\\[Nick\\] Dein neuer Name lautet nun (.+)\\.$");

  public Nickname(GrieferGames griefergames) {
    this.griefergames = griefergames;
  }

  @Subscribe
  public void messageProcessEvent(GGChatProcessEvent event) {
    if (griefergames.getSubServerType() == SubServerType.REGULAR) {
      if (event.getMessage().getPlainText().isBlank()) return;

      Matcher nicknameMsg = nicknameMsgRegex.matcher(event.getMessage().getPlainText());
      if (nicknameMsg.find()) {
        griefergames.setNickname(nicknameMsg.group(1));
      } else if (event.getMessage().getPlainText().equalsIgnoreCase("[Nick] Dein Name wurde zurückgesetzt.")) {
        griefergames.setNickname(null);
      }
    }
  }
}
