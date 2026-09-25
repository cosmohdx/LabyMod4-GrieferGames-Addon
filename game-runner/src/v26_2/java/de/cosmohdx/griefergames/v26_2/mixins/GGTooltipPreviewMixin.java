package de.cosmohdx.griefergames.v26_2.mixins;

import de.cosmohdx.griefergames.v26_2.GGPreviewTooltip;
import de.cosmohdx.griefergames.v26_2.GGTooltipContext;
import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GuiGraphicsExtractor.class, priority = 1000)
public abstract class GGTooltipPreviewMixin {
  @Unique private ItemStack ggaddon$tooltipStack;

  @Inject(method = "setTooltipForNextFrame(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V", at = @At("HEAD"))
  private void ggaddon$rememberItem(Font font, ItemStack stack, int x, int y, CallbackInfo ci) {
    this.ggaddon$tooltipStack = stack;
    GGTooltipContext.begin(stack);
  }

  @Inject(method = "setTooltipForNextFrame(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V", at = @At("RETURN"))
  private void ggaddon$forgetItem(Font font, ItemStack stack, int x, int y, CallbackInfo ci) {
    this.ggaddon$tooltipStack = null;
    GGTooltipContext.end();
  }

  @Inject(method = "setTooltipForNextFrameInternal", at = @At("HEAD"))
  private void ggaddon$addPreview(Font font, List<ClientTooltipComponent> components, int x,
      int y, ClientTooltipPositioner positioner, Identifier style, boolean focus,
      CallbackInfo ci) {
    ItemStack stack = this.ggaddon$tooltipStack != null
        ? this.ggaddon$tooltipStack : GGTooltipContext.current();
    if (stack == null || components.isEmpty()
        || components.stream().anyMatch(component -> component.getClass().getName().contains("SyntaxPreviewTooltip"))) {
      return;
    }
    GGPreviewTooltip preview = GGPreviewTooltip.create(components, stack);
    if (preview != null) {
      components.clear();
      components.add(preview);
    }
  }
}
