package de.cosmohdx.griefergames.feature.chat;

import de.cosmohdx.griefergames.GrieferGames;
import de.cosmohdx.griefergames.feature.chat.GGChatProcessEvent;
import de.cosmohdx.griefergames.core.SubServerType;
import de.cosmohdx.griefergames.core.GrieferGamesConfig;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.TextDecoration;
import net.labymod.api.event.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AntiMagicPrefix extends ChatModule {
  private final GrieferGames griefergames;
  private final Pattern antiMagicPrefixRegex = Pattern.compile("([A-Za-z\\-\\+]+) \\u2503 (~?!?\\w{1,16})");
  private final Pattern globalChatRegex = Pattern.compile("[A-Za-z\\-]+\\+? \\u2503 (~?!?\\w{1,16}) \\u00BB");

  public AntiMagicPrefix(GrieferGames griefergames) {
    this.griefergames = griefergames;
  }

  @Subscribe
  public void messageProcessEvent(GGChatProcessEvent event) {
    if (event.isCancelled()) return;
    if (!griefergames.configuration().chat().replaceMagicPrefixes()) return;

    if (event.getMessage().getFormattedText().contains("§k")) {
      // Check if player message
      Matcher matcher = antiMagicPrefixRegex.matcher(event.getMessage().getPlainText());
      if (matcher.find()) {
        Component msg = null;

        // Find message component in global chat
        for (Component component : event.getMessage().component().getChildren()) {
          String plain = this.getPlainText(component);
          Matcher m = globalChatRegex.matcher(plain);
          if (m.find()) {
            msg = component;
            break;
          }
        }
        if (msg == null) return;

        String ampReplacement = griefergames.configuration().chat().magicPrefixReplacement();
        if (ampReplacement.isBlank()) {
          ampReplacement = GrieferGamesConfig.DEFAULT_AMP_REPLACEMENT;
        }

        List<Component> msgChildren = new ArrayList<>(msg.getChildren());
        for (int i = 0; i < msgChildren.size(); i++) {
          Component component = msgChildren.get(i);
          String plain = this.getPlainText(component);
          if (plain.equalsIgnoreCase(matcher.group(1))) {
            component.style(component.style().undecorate(TextDecoration.OBFUSCATED));
            Component ampPrefix = Component.text(ampReplacement + " ", component.style());
            msgChildren.add(i, ampPrefix);
            i++;
          } else if (plain.equalsIgnoreCase(matcher.group(2))) {
            component.style(component.style().undecorate(TextDecoration.OBFUSCATED));
            break;
          }
        }
        msg.setChildren(msgChildren);
      }
    }
  }
}
