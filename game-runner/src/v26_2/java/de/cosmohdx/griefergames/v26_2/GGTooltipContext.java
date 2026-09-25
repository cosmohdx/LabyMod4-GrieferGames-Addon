package de.cosmohdx.griefergames.v26_2;

import net.minecraft.world.item.ItemStack;

/** The item whose tooltip is currently being extracted on the render thread. */
public final class GGTooltipContext {
  private static final ThreadLocal<ItemStack> STACK = new ThreadLocal<>();
  private GGTooltipContext() {}
  public static void begin(ItemStack stack) { STACK.set(stack); }
  public static ItemStack current() { return STACK.get(); }
  public static void end() { STACK.remove(); }
}
