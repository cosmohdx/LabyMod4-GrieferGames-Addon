package de.cosmohdx.griefergames.feature.server;

import de.cosmohdx.griefergames.GrieferGames;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidget;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidgetConfig;
import net.labymod.api.client.gui.hud.hudwidget.text.TextLine;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.resources.ResourceLocation;
import net.labymod.api.util.I18n;

public class SubServerHUDWidget extends TextHudWidget<TextHudWidgetConfig> {
  private GrieferGames griefergames;
  private TextLine line;

  public SubServerHUDWidget(GrieferGames griefergames) {
    super("gg_subserver");
    this.griefergames = griefergames;

    bindCategory(griefergames.getHudWidgetCategory());
    setIcon(Icon.texture(ResourceLocation.create(griefergames.namespace(), "textures/hud/subserver.png")));
  }

  //if CB .toUpperCase
  //if other helper.capitalize

  @Override
  public void load(TextHudWidgetConfig config) {
    super.load(config);
    line = createLine(I18n.translate(griefergames.namespace() + ".hudWidget.gg_subserver.name"), griefergames.state().getSubServer().isBlank() ?
      "CB Jungle" : capitalizedSubserver());
  }

  @Override
  public void onTick(boolean isEditorContext) {
    line.updateAndFlush(griefergames.state().getSubServer().isBlank() ? "CB Jungle" : capitalizedSubserver());
  }

  @Override
  public boolean isVisibleInGame() {
    return griefergames.state().isOnGrieferGames() && griefergames.configuration().enabled().get();
  }

  private String capitalizedSubserver() {
    String subserver = griefergames.state().getSubServer();
    if (subserver.startsWith("cb")) return subserver.toUpperCase();
    return griefergames.helper().capitalize(subserver);
  }
}
