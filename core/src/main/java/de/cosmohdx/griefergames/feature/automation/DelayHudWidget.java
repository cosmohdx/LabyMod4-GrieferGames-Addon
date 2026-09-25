package de.cosmohdx.griefergames.feature.automation;

import de.cosmohdx.griefergames.GrieferGames;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidget;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidgetConfig;
import net.labymod.api.client.gui.hud.hudwidget.text.TextLine;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.resources.ResourceLocation;
import net.labymod.api.util.I18n;
import java.util.concurrent.TimeUnit;

public class DelayHudWidget extends TextHudWidget<TextHudWidgetConfig> {
  private GrieferGames griefergames;
  private TextLine line;

  public DelayHudWidget(GrieferGames griefergames) {
    super("gg_delay");
    this.griefergames = griefergames;

    bindCategory(griefergames.getHudWidgetCategory());
    setIcon(Icon.texture(ResourceLocation.create(griefergames.namespace(), "textures/hud/clock.png")));
  }

  @Override
  public void load(TextHudWidgetConfig config) {
    super.load(config);
    line = createLine(I18n.translate(griefergames.namespace()+".hudWidget.gg_delay.name"), "0s");
  }

  @Override
  public void onTick(boolean isEditorContext) {
    if(isEditorContext) {
      line.updateAndFlush("10s");
    } else {
      long remainingTime = griefergames.getWaitTime() - System.currentTimeMillis();

      if(remainingTime < 1) {
        griefergames.setCitybuildDelay(false);
      }

      String formattedTime = "";
      if(remainingTime > 60000) {
        remainingTime /= 1000;
        int minutes = (int)Math.floor((double)remainingTime / 60);
        int seconds = (int)(remainingTime % 60);
        formattedTime = minutes+":"+seconds;
      } else {
        formattedTime = TimeUnit.MILLISECONDS.toSeconds(remainingTime) + "s";
      }
      line.updateAndFlush((griefergames.isCitybuildDelay() ? "§c" : "") + formattedTime);
    }
  }

  @Override
  public boolean isVisibleInGame() {
    return griefergames.isOnGrieferGames() && griefergames.configuration().enabled().get() && griefergames.getWaitTime() > System.currentTimeMillis() + 1000;
  }
}
