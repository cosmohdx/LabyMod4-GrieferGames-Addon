package de.cosmohdx.griefergames.v1_17_1.mixins;

import de.cosmohdx.griefergames.feature.itempreview.EnchantmentGlintItems;
import de.cosmohdx.griefergames.feature.itempreview.EnchantmentGlintPass;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.core.Registry;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemStack;
import com.mojang.blaze3d.vertex.PoseStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntityWithoutLevelRenderer.class)
public class MixinBlockEntityWithoutLevelRenderer {

  @Inject(method = "renderByItem", at = @At("HEAD"))
  private void griefergames$start(ItemStack stack, net.minecraft.client.renderer.block.model.ItemTransforms.TransformType context, PoseStack pose, MultiBufferSource buffer, int light, int overlay, CallbackInfo ci) {
    EnchantmentGlintPass.request(stack.hasFoil() && EnchantmentGlintItems.applies(Registry.ITEM.getKey(stack.getItem()).getPath()));
  }

  @Inject(method = "renderByItem", at = @At("RETURN"))
  private void griefergames$end(ItemStack stack, net.minecraft.client.renderer.block.model.ItemTransforms.TransformType context, PoseStack pose, MultiBufferSource buffer, int light, int overlay, CallbackInfo ci) {
    EnchantmentGlintPass.clear();
  }
}
