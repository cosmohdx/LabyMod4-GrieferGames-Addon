package de.cosmohdx.griefergames.v1_8_9.mixins;

import de.cosmohdx.griefergames.feature.itempreview.EnchantmentGlintPass;
import de.cosmohdx.griefergames.v1_8_9.LegacyItemGlint;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderItem.class)
public class MixinRenderItem {

  @Inject(
      method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/resources/model/IBakedModel;)V",
      at = @At(
          value = "INVOKE",
          target = "Lnet/minecraft/client/renderer/tileentity/TileEntityItemStackRenderer;renderByItem(Lnet/minecraft/item/ItemStack;)V",
          shift = At.Shift.AFTER
      )
  )
  private void griefergames$glint(ItemStack stack, IBakedModel model, CallbackInfo ci) {
    if (stack == null || !stack.hasEffect() || !EnchantmentGlintPass.enabled() || !special(stack.getItem())) {
      return;
    }
    LegacyItemGlint.drawCube();
  }

  private static boolean special(Item item) {
    return item == Items.skull
        || item == Items.banner
        || item == Item.getItemFromBlock(Blocks.chest)
        || item == Item.getItemFromBlock(Blocks.trapped_chest)
        || item == Item.getItemFromBlock(Blocks.ender_chest);
  }
}
