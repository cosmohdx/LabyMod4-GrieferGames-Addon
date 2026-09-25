package de.cosmohdx.griefergames.feature.server;

import de.cosmohdx.griefergames.GrieferGames;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidget;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidgetConfig;
import net.labymod.api.client.gui.hud.hudwidget.text.TextLine;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.resources.ResourceLocation;
import net.labymod.api.util.I18n;

public class RedstoneHudWidget extends TextHudWidget<TextHudWidgetConfig>  {
  private GrieferGames griefergames;
  private TextLine line;

  public RedstoneHudWidget(GrieferGames griefergames) {
    super("gg_redstone");
    this.griefergames = griefergames;

    bindCategory(griefergames.getHudWidgetCategory());
    setIcon(Icon.texture(ResourceLocation.create(griefergames.namespace(), "textures/hud/redstone.png")));
  }

  @Override
  public void load(TextHudWidgetConfig config) {
    super.load(config);
    line = createLine(I18n.translate(griefergames.namespace()+".hudWidget.gg_redstone.name"), I18n.translate(griefergames.namespace()+".messages.off"));
  }

  @Override
  public void onTick(boolean isEditorContext) {
    line.updateAndFlush(griefergames.isRedstoneActive() ? I18n.translate(griefergames.namespace()+".messages.on") : I18n.translate(griefergames.namespace()+".messages.off"));
  }

  @Override
  public boolean isVisibleInGame() {
    return griefergames.isOnGrieferGames() && griefergames.configuration().enabled().get();
  }
}
