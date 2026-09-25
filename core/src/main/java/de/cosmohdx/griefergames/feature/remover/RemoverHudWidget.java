package de.cosmohdx.griefergames.feature.remover;

import de.cosmohdx.griefergames.GrieferGames;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidget;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidgetConfig;
import net.labymod.api.client.gui.hud.hudwidget.text.TextLine;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.resources.ResourceLocation;
import net.labymod.api.util.I18n;

public class RemoverHudWidget extends TextHudWidget<TextHudWidgetConfig> {

  private final GrieferGames griefergames;
  private final String widgetId;
  private final boolean items;
  private TextLine line;

  private RemoverHudWidget(GrieferGames griefergames, String widgetId, String icon, boolean items) {
    super(widgetId);
    this.griefergames = griefergames;
    this.widgetId = widgetId;
    this.items = items;
    bindCategory(griefergames.getHudWidgetCategory());
    setIcon(Icon.texture(ResourceLocation.create(griefergames.namespace(), icon)));
  }

  public static RemoverHudWidget items(GrieferGames griefergames) {
    return new RemoverHudWidget(griefergames, "gg_item_remover", "textures/itemremover.png", true);
  }

  public static RemoverHudWidget entities(GrieferGames griefergames) {
    return new RemoverHudWidget(griefergames, "gg_entity_remover", "textures/mobremover.png", false);
  }

  @Override
  public void load(TextHudWidgetConfig config) {
    super.load(config);
    this.line = createLine(
        I18n.translate(this.griefergames.namespace() + ".hudWidget." + this.widgetId + ".name"),
        "1:30");
  }

  @Override
  public void onTick(boolean isEditorContext) {
    if (isEditorContext) {
      this.line.updateAndFlush("1:30");
      return;
    }
    long remaining = this.griefergames.remover().remainingMillis(this.items);
    if (remaining < 0) {
      return;
    }
    this.line.updateAndFlush(format(remaining));
  }

  @Override
  public boolean isVisibleInGame() {
    return this.griefergames.state().isOnGrieferGames()
        && this.griefergames.configuration().enabled().get()
        && this.griefergames.configuration().remover().isEnabled()
        && this.griefergames.remover().remainingMillis(this.items) >= 0;
  }

  static String format(long remainingMillis) {
    long totalSeconds = remainingMillis / 1000;
    long hours = totalSeconds / 3600;
    long minutes = (totalSeconds % 3600) / 60;
    long seconds = totalSeconds % 60;
    if (hours > 0) {
      return hours + ":" + pad(minutes) + ":" + pad(seconds);
    }
    return minutes + ":" + pad(seconds);
  }

  private static String pad(long value) {
    return value < 10 ? "0" + value : Long.toString(value);
  }
}
