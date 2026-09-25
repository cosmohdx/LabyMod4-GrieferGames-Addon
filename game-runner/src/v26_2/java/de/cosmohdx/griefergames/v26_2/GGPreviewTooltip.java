package de.cosmohdx.griefergames.v26_2;

import de.cosmohdx.griefergames.v26_2.TooltipFeatures;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.state.MapRenderState;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.joml.Matrix3x2fStack;

/** Adds an image beside the existing tooltip text, only while the item is hovered. */
public final class GGPreviewTooltip implements ClientTooltipComponent {
  private static final int PREVIEW_SIZE = 64;
  private static final int TEXT_OFFSET = PREVIEW_SIZE + 8;

  private final List<ClientTooltipComponent> content;
  private final ItemStack head;
  private final MapRenderState map;

  private GGPreviewTooltip(List<ClientTooltipComponent> content, ItemStack head,
      MapRenderState map) {
    this.content = List.copyOf(content);
    this.head = head;
    this.map = map;
  }

  public static GGPreviewTooltip create(List<ClientTooltipComponent> content, ItemStack stack) {
    Minecraft minecraft = Minecraft.getInstance();
    if (TooltipFeatures.mapTooltipPreviewEnabled() && stack.getItem() instanceof MapItem
        && minecraft.level != null) {
      MapId id = stack.get(DataComponents.MAP_ID);
      if (id != null) {
        MapItemSavedData data = minecraft.level.getMapData(id);
        if (data != null) {
          MapRenderState state = new MapRenderState();
          minecraft.getMapRenderer().extractRenderState(id, data, state);
          return new GGPreviewTooltip(content, null, state);
        }
      }
    }
    if (TooltipFeatures.headTooltipPreviewEnabled()
        && Block.byItem(stack.getItem()) instanceof AbstractSkullBlock) {
      return new GGPreviewTooltip(content, stack, null);
    }
    return null;
  }

  @Override
  public int getHeight(Font font) {
    int height = 0;
    for (int i = 0; i < this.content.size(); i++) {
      height += this.content.get(i).getHeight(font) + (i == 0 ? 2 : 0);
    }
    // Minecraft subtracts two pixels from a tooltip with only one component.
    return Math.max(PREVIEW_SIZE, height) + 2;
  }

  @Override
  public int getWidth(Font font) {
    int width = 0;
    for (ClientTooltipComponent component : this.content) {
      width = Math.max(width, component.getWidth(font));
    }
    return TEXT_OFFSET + width;
  }

  @Override
  public void extractText(GuiGraphicsExtractor graphics, Font font, int x, int y) {
    int rowY = y;
    for (int i = 0; i < this.content.size(); i++) {
      ClientTooltipComponent component = this.content.get(i);
      component.extractText(graphics, font, x + TEXT_OFFSET, rowY);
      rowY += component.getHeight(font) + (i == 0 ? 2 : 0);
    }
  }

  @Override
  public void extractImage(Font font, int x, int y, int width, int height,
      GuiGraphicsExtractor graphics) {
    Matrix3x2fStack pose = graphics.pose();
    pose.pushMatrix();
    pose.translate(x, y);
    if (this.map != null) {
      pose.scale(0.5F, 0.5F);
      graphics.map(this.map);
    } else if (this.head != null) {
      pose.scale(4.0F, 4.0F);
      graphics.item(this.head, 0, 0);
    }
    pose.popMatrix();

    int rowY = y;
    for (int i = 0; i < this.content.size(); i++) {
      ClientTooltipComponent component = this.content.get(i);
      component.extractImage(font, x + TEXT_OFFSET, rowY,
          width - TEXT_OFFSET, height, graphics);
      rowY += component.getHeight(font) + (i == 0 ? 2 : 0);
    }
  }
}
