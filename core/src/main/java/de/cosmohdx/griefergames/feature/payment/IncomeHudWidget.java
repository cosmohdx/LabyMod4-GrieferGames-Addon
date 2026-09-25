package de.cosmohdx.griefergames.feature.payment;

import de.cosmohdx.griefergames.GrieferGames;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidget;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidgetConfig;
import net.labymod.api.client.gui.hud.hudwidget.text.TextLine;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.resources.ResourceLocation;
import net.labymod.api.util.I18n;

public class IncomeHudWidget extends TextHudWidget<TextHudWidgetConfig> {
  private GrieferGames griefergames;
  private TextLine line;

  public IncomeHudWidget(GrieferGames griefergames) {
    super("gg_income");
    this.griefergames = griefergames;

    bindCategory(griefergames.getHudWidgetCategory());
    setIcon(Icon.texture(ResourceLocation.create(
        griefergames.namespace(),
        "textures/hud/economy_cash.png"
    )));
  }

  @Override
  public void load(TextHudWidgetConfig config) {
    super.load(config);
    line = createLine(I18n.translate(griefergames.namespace()+".hudWidget.gg_income.name"), "$0");
  }

  @Override
  public void onTick(boolean isEditorContext) {
    if(griefergames.state().getIncome() >= 0) {
      line.updateAndFlush("$"+griefergames.state().getIncome());
    } else {
      line.updateAndFlush("§c$"+griefergames.state().getIncome());
    }
  }

  @Override
  public boolean isVisibleInGame() {
    return griefergames.state().isOnGrieferGames() && griefergames.configuration().enabled().get() && griefergames.state().getIncome() != 0;
  }
}
