package de.cosmohdx.griefergames.feature.chat;

import de.cosmohdx.griefergames.GrieferGames;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.TextComponent;
import net.labymod.api.client.entity.player.tag.TagType;
import net.labymod.api.client.network.NetworkPlayerInfo;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.render.PlayerNameTagRenderEvent;

public class GGNameTagListener {

  private final GrieferGames griefergames;

  public GGNameTagListener(GrieferGames griefergames) {
    this.griefergames = griefergames;
  }

  @Subscribe
  public void onRender(PlayerNameTagRenderEvent event) {
    if (!griefergames.state().isOnGrieferGames()) return;
    if (!griefergames.configuration().chatConfig().isShowPrefixInDisplayName()) return;
    NetworkPlayerInfo playerInfo = event.getPlayerInfo();
    if (playerInfo == null) return;
    if (event.tagType() != TagType.MAIN_TAG) return;
    if (playerInfo.displayName() instanceof TextComponent) {
      event.setNameTag(removeMarker((TextComponent) playerInfo.displayName()));
    }
  }

  private TextComponent removeMarker(TextComponent textComponent) {
    if (textComponent.getText().equals(" ✎")) {
      textComponent.text("");
    }
    for (Component component : textComponent.getChildren()) {
      if (component instanceof TextComponent) {
        removeMarker((TextComponent) component);
      }
    }
    return textComponent;
  }
}