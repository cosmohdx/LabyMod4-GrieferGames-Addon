package de.cosmohdx.griefergames.v26_2.mixins;

import de.cosmohdx.griefergames.v26_2.TooltipFeatures;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Restores a foil state for legacy enchanted block entity items whose enchantment is in stored/custom data. */
@Mixin(ItemStack.class)
public abstract class GGHeadGlintMixin {
  @Inject(method = "hasFoil", at = @At("RETURN"), cancellable = true)
  private void ggaddon$legacyHeadGlint(CallbackInfoReturnable<Boolean> result) {
    if (result.getReturnValue() || !TooltipFeatures.headEnchantmentGlintEnabled()) {
      return;
    }
    ItemStack stack = (ItemStack) (Object) this;
    if (!(Block.byItem(stack.getItem()) instanceof BaseEntityBlock)) {
      return;
    }
    ItemEnchantments direct = stack.get(DataComponents.ENCHANTMENTS);
    if (direct != null && !direct.isEmpty()) {
      result.setReturnValue(true);
      return;
    }
    ItemEnchantments stored = stack.get(DataComponents.STORED_ENCHANTMENTS);
    if (stored != null && !stored.isEmpty()) {
      result.setReturnValue(true);
      return;
    }
    CustomData data = stack.get(DataComponents.CUSTOM_DATA);
    if (data != null && !data.isEmpty()) {
      var tag = data.copyTag();
      if (!tag.getListOrEmpty("ench").isEmpty()
          || !tag.getListOrEmpty("Enchantments").isEmpty()
          || !tag.getListOrEmpty("StoredEnchantments").isEmpty()) {
        result.setReturnValue(true);
      }
    }
  }
}
