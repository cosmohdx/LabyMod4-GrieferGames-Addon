package de.cosmohdx.griefergames.feature.chat;

import de.cosmohdx.griefergames.GrieferGames;
import de.cosmohdx.griefergames.feature.chat.GGChatProcessEvent;
import de.cosmohdx.griefergames.core.SubServerType;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.TextDecoration;
import net.labymod.api.event.Subscribe;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AntiMagicClanTag extends ChatModule {
  private final GrieferGames griefergames;
  private final Pattern antiMagicClanTagRegex = Pattern.compile("\\[[^\\]]+\\] ([A-Za-z\\-\\+]+) \\u2503 (~?!?\\w{1,16})");

  public AntiMagicClanTag(GrieferGames griefergames) {
    this.griefergames = griefergames;
  }

  @Subscribe
  public void messageProcessEvent(GGChatProcessEvent event) {
    if (event.isCancelled()) return;
    if (!griefergames.configuration().chatConfig().isAmpClantagEnabled()) return;

    String formatted = event.getMessage().getFormattedText();
    if (formatted.contains("§k") || formatted.contains("§m")) {
      Matcher matcher = antiMagicClanTagRegex.matcher(event.getMessage().getPlainText());
      if (matcher.find()) {
        for (Component clanTag : event.getMessage().component().getChildren()) {
          String formattedClanTag = this.getFormattedText(clanTag);
          if (formattedClanTag.contains("§6[") && formattedClanTag.contains("§6]")) {
            for (Component component : clanTag.getChildren()) {
              component.style(component.style().undecorate(TextDecoration.OBFUSCATED, TextDecoration.STRIKETHROUGH));
            }
          }
        }
      }
    }
  }
}
