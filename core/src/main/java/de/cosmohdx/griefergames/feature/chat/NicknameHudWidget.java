package de.cosmohdx.griefergames.feature.chat;

import de.cosmohdx.griefergames.GrieferGames;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidget;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidgetConfig;
import net.labymod.api.client.gui.hud.hudwidget.text.TextLine;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.resources.ResourceLocation;
import net.labymod.api.util.I18n;

public class NicknameHudWidget extends TextHudWidget<TextHudWidgetConfig> {
  private GrieferGames griefergames;
  private TextLine line;

  public NicknameHudWidget(GrieferGames griefergames) {
    super("gg_nickname");
    this.griefergames = griefergames;

    bindCategory(griefergames.getHudWidgetCategory());
    setIcon(Icon.texture(ResourceLocation.create(griefergames.namespace(), "textures/hud/nickname.png")));
  }

  @Override
  public void load(TextHudWidgetConfig config) {
    super.load(config);
    line = createLine(I18n.translate(griefergames.namespace()+".hudWidget.gg_nickname.name"), I18n.translate(griefergames.namespace()+".hudWidget.gg_nickname.defaultValue"));
  }

  @Override
  public void onTick(boolean isEditorContext) {
    if(griefergames.getNickname() != null) {
      line.updateAndFlush(griefergames.getNickname());
    } else {
      line.updateAndFlush(I18n.translate(griefergames.namespace()+".hudWidget.gg_nickname.defaultValue"));
    }
  }

  @Override
  public boolean isVisibleInGame() {
    return griefergames.isOnGrieferGames() && griefergames.configuration().enabled().get() && griefergames.getNickname() != null;
  }
}
