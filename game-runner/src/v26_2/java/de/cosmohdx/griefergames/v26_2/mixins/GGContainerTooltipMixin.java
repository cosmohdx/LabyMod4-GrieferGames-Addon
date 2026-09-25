package de.cosmohdx.griefergames.v26_2.mixins;

import de.cosmohdx.griefergames.v26_2.GGTooltipContext;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Inventory screens render tooltips through the list overload, so capture the hovered slot. */
@Mixin(AbstractContainerScreen.class)
public abstract class GGContainerTooltipMixin {
  @Shadow protected Slot hoveredSlot;

  @Inject(method = "extractTooltip", at = @At("HEAD"))
  private void ggaddon$beginTooltip(GuiGraphicsExtractor graphics, int x, int y, CallbackInfo ci) {
    if (this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
      GGTooltipContext.begin(this.hoveredSlot.getItem());
    }
  }

  @Inject(method = "extractTooltip", at = @At("RETURN"))
  private void ggaddon$endTooltip(GuiGraphicsExtractor graphics, int x, int y, CallbackInfo ci) {
    GGTooltipContext.end();
  }
}
