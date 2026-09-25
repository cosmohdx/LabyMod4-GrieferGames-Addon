package de.cosmohdx.griefergames.v26_2.mixins;

import de.cosmohdx.griefergames.v26_2.HomePlotCount;
import de.cosmohdx.griefergames.v26_2.TooltipFeatures;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Renders real overstack counts or the explicit plot total on GrieferGames menu axes. */
@Mixin(GuiGraphicsExtractor.class)
public abstract class GGOverstackCountMixin {
  @Shadow public abstract void text(Font font, String value, int x, int y, int color,
      boolean shadow);

  @Inject(method = "itemCount", at = @At("HEAD"), cancellable = true)
  private void ggaddon$itemBadge(Font font, ItemStack stack, int x, int y,
      String replacement, CallbackInfo ci) {
    if (stack.isEmpty()) return;
    int amount = 0;
    if (TooltipFeatures.overstackingFixEnabled() && stack.getCount() > 1
        && stack.getItem().getDefaultMaxStackSize() == 1) {
      amount = stack.getCount();
    } else if (TooltipFeatures.overstackingFixEnabled() && stack.getCount() == 1
        && stack.getItem() instanceof AxeItem && isGrieferGames()) {
      ItemLore lore = stack.get(DataComponents.LORE);
      if (lore != null) {
        for (var line : lore.lines()) {
          amount = HomePlotCount.fromLoreLine(line.getString());
          if (amount > 1) break;
        }
      }
    }
    if (amount <= 1) return;
    String shown = Integer.toString(amount);
    this.text(font, shown, x + 17 - font.width(shown), y + 9, 0xFFFFFFFF, true);
    ci.cancel();
  }

  private static boolean isGrieferGames() {
    var server = Minecraft.getInstance().getCurrentServer();
    return server != null && server.ip != null
        && server.ip.toLowerCase(Locale.ROOT).contains("griefergames.net");
  }
}
