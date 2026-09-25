package de.cosmohdx.griefergames.feature.nearby;

import de.cosmohdx.griefergames.GrieferGames;
import java.util.List;
import java.util.UUID;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.NamedTextColor;
import net.labymod.api.client.gfx.pipeline.renderer.text.TextRenderingOptions;
import net.labymod.api.client.gui.hud.hudwidget.HudWidgetConfig;
import net.labymod.api.client.gui.hud.hudwidget.SimpleHudWidget;
import net.labymod.api.client.gui.hud.position.HudSize;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.gui.screen.ScreenContext;
import net.labymod.api.client.render.font.RenderableComponent;
import net.labymod.api.client.resources.ResourceLocation;
import net.labymod.api.util.I18n;

public class NearbyPlayersHudWidget extends SimpleHudWidget<HudWidgetConfig> {

  private static final int ICON_SIZE = 12;
  private static final int GAP = 2;
  private static final int ROW_GAP = 1;

  private final GrieferGames griefergames;
  private final NearbyPlayersService service;

  public NearbyPlayersHudWidget(GrieferGames griefergames, NearbyPlayersService service) {
    super("gg_nearby_players", HudWidgetConfig.class);
    this.griefergames = griefergames;
    this.service = service;
    bindCategory(griefergames.getHudWidgetCategory());
    setIcon(Icon.texture(ResourceLocation.create(griefergames.namespace(), "textures/hud/nickname.png")));
  }

  @Override
  public void render(RenderPhase phase, ScreenContext screenContext, boolean isEditorContext, HudSize size) {
    List<NearbyPlayer> rows = rows(isEditorContext);
    boolean showDistance = this.griefergames.configuration().nearbyPlayers().distanceDisplay() == DistanceDisplay.COARSE;
    boolean rightBound = anchor().isRight();

    RenderableComponent header = RenderableComponent.of(Component.text(headerText(rows.size()), NamedTextColor.WHITE));
    float width = header.getWidth();
    RenderableComponent[] labels = new RenderableComponent[rows.size()];
    for (int i = 0; i < rows.size(); i++) {
      labels[i] = RenderableComponent.of(line(rows.get(i), showDistance));
      width = Math.max(width, ICON_SIZE + GAP + labels[i].getWidth());
    }

    float y = 0.0F;
    if (phase.canRender()) {
      float headerX = rightBound ? width - header.getWidth() : 0.0F;
      screenContext.canvas().submitRenderableComponent(header, headerX, y, -1, TextRenderingOptions.SHADOW);
    }
    y += header.getHeight() + ROW_GAP;

    for (int i = 0; i < rows.size(); i++) {
      NearbyPlayer player = rows.get(i);
      RenderableComponent label = labels[i];
      float rowHeight = Math.max(ICON_SIZE, label.getHeight());
      float iconX = rightBound ? width - ICON_SIZE : 0.0F;
      float textX = rightBound ? width - ICON_SIZE - GAP - label.getWidth() : ICON_SIZE + GAP;
      float textY = y + (rowHeight - label.getHeight()) / 2.0F;
      float iconY = y + (rowHeight - ICON_SIZE) / 2.0F;
      if (phase.canRender()) {
        Icon icon = head(player.name());
        if (icon != null) {
          screenContext.canvas().submitIcon(icon, iconX, iconY, ICON_SIZE, ICON_SIZE);
        }
        screenContext.canvas().submitRenderableComponent(label, textX, textY, -1, TextRenderingOptions.SHADOW);
      }
      y += rowHeight + ROW_GAP;
    }

    size.set((int) Math.ceil(width), (int) Math.ceil(y));
  }

  @Override
  public boolean isVisibleInGame() {
    return this.griefergames.state().isOnGrieferGames()
        && this.griefergames.configuration().enabled().get()
        && this.service.isActive();
  }

  private List<NearbyPlayer> rows(boolean editor) {
    List<NearbyPlayer> players = this.service.players();
    if (editor && players.isEmpty()) {
      return List.of(
          new NearbyPlayer(new UUID(0L, 1L), "Steve", Component.text("Steve"), 3.0D),
          new NearbyPlayer(new UUID(0L, 2L), "Alex", Component.text("Alex"), 12.0D));
    }
    return players;
  }

  private String headerText(int count) {
    return I18n.translate(this.griefergames.namespace() + ".hudWidget.gg_nearby_players.name") + ": " + count;
  }

  private static Component line(NearbyPlayer player, boolean showDistance) {
    Component name = player.displayName() != null ? player.displayName().copy() : Component.text(player.name());
    Component line = Component.empty().append(name);
    if (showDistance) {
      line = line.append(Component.text(" " + NearbyDistance.coarse(player.distance()), NamedTextColor.GRAY));
    }
    return line;
  }

  private static Icon head(String name) {
    if (name == null || name.isBlank()) {
      return null;
    }
    return Icon.head(name);
  }
}
