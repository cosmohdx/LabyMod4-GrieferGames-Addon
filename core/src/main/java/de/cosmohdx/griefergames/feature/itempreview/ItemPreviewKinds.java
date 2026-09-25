package de.cosmohdx.griefergames.feature.itempreview;

import net.labymod.api.client.resources.ResourceLocation;
import net.labymod.api.client.world.item.ItemStack;

public final class ItemPreviewKinds {

  private ItemPreviewKinds() {
  }

  public static boolean isFilledMap(ItemStack stack) {
    String path = path(stack);
    return "filled_map".equals(path);
  }

  public static boolean isPlayerHead(ItemStack stack) {
    String path = path(stack);
    if ("player_head".equals(path)) {
      return true;
    }
    if ("skull".equals(path) || "skull_item".equals(path)) {
      return stack.getLegacyItemData() == 3;
    }
    return false;
  }

  public static String path(ItemStack stack) {
    if (stack == null || stack.isAir()) {
      return "";
    }
    ResourceLocation identifier = stack.getIdentifier();
    if (identifier == null || identifier.getPath() == null) {
      return "";
    }
    return identifier.getPath();
  }
}
