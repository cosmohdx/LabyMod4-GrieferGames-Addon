package de.cosmohdx.griefergames.feature.blockoftheday;

import de.cosmohdx.griefergames.GrieferGames;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidget;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidgetConfig;
import net.labymod.api.client.gui.hud.hudwidget.text.TextLine;
import net.labymod.api.util.I18n;

public class BlockOfTheDayHudWidget extends TextHudWidget<TextHudWidgetConfig> {

  private final GrieferGames griefergames;
  private TextLine line;

  public BlockOfTheDayHudWidget(GrieferGames griefergames) {
    super("gg_block_of_the_day");
    this.griefergames = griefergames;
    bindCategory(griefergames.getHudWidgetCategory());
  }

  @Override
  public void load(TextHudWidgetConfig config) {
    super.load(config);
    this.line = createLine(
        I18n.translate(this.griefergames.namespace() + ".hudWidget.gg_block_of_the_day.name"),
        "Diamond Ore (3)");
  }

  @Override
  public void onTick(boolean isEditorContext) {
    if (isEditorContext) {
      this.line.updateAndFlush("Diamond Ore (3)");
      return;
    }
    if (!this.griefergames.blockOfTheDay().known()) {
      return;
    }
    this.line.updateAndFlush(this.griefergames.blockOfTheDay().hudValue());
  }

  @Override
  public boolean isVisibleInGame() {
    return this.griefergames.state().isOnGrieferGames()
        && this.griefergames.configuration().enabled().get()
        && this.griefergames.blockOfTheDay().known();
  }
}
