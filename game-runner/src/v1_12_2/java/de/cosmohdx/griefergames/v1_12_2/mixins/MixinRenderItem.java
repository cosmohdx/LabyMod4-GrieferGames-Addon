package de.cosmohdx.griefergames.v1_12_2.mixins;

import de.cosmohdx.griefergames.feature.itempreview.EnchantmentGlintPass;
import de.cosmohdx.griefergames.v1_12_2.LegacyItemGlint;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
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
      method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/block/model/IBakedModel;)V",
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
    return item == Items.SKULL
        || item == Items.BANNER
        || item == Item.getItemFromBlock(Blocks.CHEST)
        || item == Item.getItemFromBlock(Blocks.TRAPPED_CHEST)
        || item == Item.getItemFromBlock(Blocks.ENDER_CHEST);
  }
}
