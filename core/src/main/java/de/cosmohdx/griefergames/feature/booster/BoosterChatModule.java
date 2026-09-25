package de.cosmohdx.griefergames.feature.booster;

import de.cosmohdx.griefergames.GrieferGames;
import de.cosmohdx.griefergames.feature.chat.ChatModule;
import de.cosmohdx.griefergames.feature.chat.GGChatProcessEvent;
import de.cosmohdx.griefergames.core.SubServerType;
import net.labymod.api.event.Subscribe;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BoosterChatModule extends ChatModule {
  private final GrieferGames griefergames;
  private final Pattern boosterInfoRegex = Pattern.compile("^([A-z]+-Booster): (?:(Deaktiviert)|([0-9+]+)x Multiplikator (\\(.+\\)\\s?)+)$");
  private final Pattern boosterStartRegex = Pattern.compile("^\\[Booster\\] .+ hat für die GrieferGames Community den ([A-z]+-Booster) für ([0-9]+) Minuten aktiviert\\.$");
  private final Pattern boosterEndRegex = Pattern.compile("^\\[Booster\\] Der ([A-z]+-Booster) \\(Stufe [0-9]+\\) von .+ ist abgelaufen\\.$");
  private final Pattern boosterResetRegex = Pattern.compile("^\\[Booster\\] Der ([A-z]+-Booster) ist jetzt wieder deaktiviert\\.$");
  private Pattern durationRegex = Pattern.compile("(?:([0-9]+):)?([0-9]+):([0-9]+)");

  public BoosterChatModule(GrieferGames griefergames) {
    this.griefergames = griefergames;
  }

  @Subscribe
  public void messageProcessEvent(GGChatProcessEvent event) {
    if (griefergames.state().getSubServerType() == SubServerType.REGULAR) {
      String plain = event.getMessage().getPlainText();

      Matcher matcher = boosterInfoRegex.matcher(plain);
      if (matcher.find()) {
        String type = matcher.group(1);
        if (matcher.group(2) == null) {
          try {
            int count = Integer.parseInt(matcher.group(3));
            List<Long> durations = new ArrayList<>();

            for (String durationStr : matcher.group(4).split(" ")) {
              Matcher durationMatcher = durationRegex.matcher(durationStr);
              if (durationMatcher.find()) {
                int seconds = Integer.parseInt(durationMatcher.group(3));
                int minutes = Integer.parseInt(durationMatcher.group(2));
                int hours = (durationMatcher.group(1) != null) ? Integer.parseInt(durationMatcher.group(1)) : 0;

                durations.add(TimeUnit.SECONDS.toMillis(seconds) + TimeUnit.MINUTES.toMillis(minutes) + TimeUnit.HOURS.toMillis(hours));
              }
            }

            Collections.reverse(durations);
            griefergames.boosterController().setBooster(type, count, durations);
          } catch (NumberFormatException e) {
            e.printStackTrace();
          }
        } else {
          griefergames.boosterController().resetBooster(type);
        }
        return;
      }

      matcher = boosterStartRegex.matcher(plain);
      if (matcher.find()) {
        String type = matcher.group(1);
        try {
          int minutes = Integer.parseInt(matcher.group(2));

          griefergames.boosterController().addBooster(type, TimeUnit.MINUTES.toMillis(minutes));
        } catch (NumberFormatException e) {
          e.printStackTrace();
        }
        return;
      }

      matcher = boosterEndRegex.matcher(plain);
      if (matcher.find()) {
        String type = matcher.group(1);
        griefergames.boosterController().removeBooster(type);
        return;
      }

      matcher = boosterResetRegex.matcher(plain);
      if (matcher.find()) {
        String type = matcher.group(1);
        griefergames.boosterController().resetBooster(type);
      }
    }
  }
}
