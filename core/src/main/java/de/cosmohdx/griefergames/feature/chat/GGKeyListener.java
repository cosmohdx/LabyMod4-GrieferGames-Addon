package de.cosmohdx.griefergames.feature.chat;

import de.cosmohdx.griefergames.GrieferGames;
import de.cosmohdx.griefergames.core.SubServerType;
import de.cosmohdx.griefergames.core.GrieferGamesConfig;
import net.labymod.api.Laby;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.TextDecoration;
import net.labymod.api.client.network.NetworkPlayerInfo;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.input.KeyEvent;
import net.labymod.api.event.client.input.KeyEvent.State;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GGKeyListener {
  private final GrieferGames griefergames;
  private final Pattern antiMagicPrefixRegex = Pattern.compile("([A-Za-z\\-]+\\+?) \\u2503 (\\u007E?\\!?\\w{1,16})");

  public GGKeyListener(GrieferGames griefergames) {
    this.griefergames = griefergames;
  }

  @Subscribe
  public void onKeyInput(KeyEvent event) {
    if(!griefergames.state().isOnGrieferGames()) return;

    if(griefergames.state().getSubServerType() == SubServerType.REGULAR) {
      if(event.state() == State.PRESS) {
        if(griefergames.configuration().chat().replaceMagicPrefixes() &&
            event.key().getId() == Laby.labyAPI().minecraft().options().getInputMapping("key.playerlist").getKeyCode()) {

          for(NetworkPlayerInfo playerInfo : Laby.labyAPI().minecraft().getClientPacketListener().getNetworkPlayerInfos()) {
            if(playerInfo == null) continue;
            if(!griefergames.helper().componentToFormattedText(playerInfo.displayName()).contains("§k")) continue;

            Matcher matcher = antiMagicPrefixRegex.matcher(griefergames.helper().componentToPlainText(playerInfo.displayName()));
            if(!matcher.find()) continue;

            String ampReplacement = griefergames.configuration().chat().magicPrefixReplacement();
            if(ampReplacement.isBlank()) {
              ampReplacement = GrieferGamesConfig.DEFAULT_AMP_REPLACEMENT;
            }

            List<Component> children = new ArrayList<>(playerInfo.displayName().getChildren());
            for(int i=0; i<children.size(); i++) {
              Component component = children.get(i);
              String plain = griefergames.helper().componentToPlainText(component);

              if(plain.equals(matcher.group(1))) {
                component.style(component.style().undecorate(TextDecoration.OBFUSCATED));
                Component ampPrefix = Component.text(ampReplacement+" ", component.style());
                children.add(i, ampPrefix);
                i++;
              } else if(plain.equals(matcher.group(2))) {
                component.style(component.style().undecorate(TextDecoration.OBFUSCATED));
              }
            }
            playerInfo.displayName().setChildren(children);
          }
        }
      }
    }
  }
}
