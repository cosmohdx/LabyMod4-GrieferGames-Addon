package de.cosmohdx.griefergames.feature.automation;

import de.cosmohdx.griefergames.GrieferGames;
import de.cosmohdx.griefergames.feature.chat.ChatModule;
import de.cosmohdx.griefergames.feature.chat.GGChatProcessEvent;
import de.cosmohdx.griefergames.core.SubServerType;
import net.labymod.api.event.Subscribe;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WaitTime extends ChatModule {
  private final GrieferGames griefergames;
  private final Pattern cityBuildDelayRegex = Pattern.compile("Der Server konnte deine Daten noch nicht verarbeiten\\. Du wurdest für (\\d+) Minuten gesperrt!");

  public WaitTime(GrieferGames griefergames) {
    this.griefergames = griefergames;
  }

  @Subscribe
  public void messageProcessEvent(GGChatProcessEvent event) {
    if (griefergames.getSubServerType() == SubServerType.REGULAR) {
      String plain = event.getMessage().getPlainText();
      if (plain.isBlank()) return;

      if (plain.startsWith("Der Server ist voll.") || plain.equalsIgnoreCase("Der Server ist gerade im Wartungsmodus.")) {
        if (!griefergames.isCitybuildDelay()) {
          griefergames.setWaitTime(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(12));
        }
      }

      Matcher matcher = cityBuildDelayRegex.matcher(plain);
      if (matcher.find()) {
        try {
          long delay = TimeUnit.MINUTES.toMillis(Integer.parseInt(matcher.group(1)));
          griefergames.setWaitTime(System.currentTimeMillis() + delay);
          griefergames.setCitybuildDelay(true);
        } catch (NumberFormatException e) {
        }
      }
    }
  }
}
