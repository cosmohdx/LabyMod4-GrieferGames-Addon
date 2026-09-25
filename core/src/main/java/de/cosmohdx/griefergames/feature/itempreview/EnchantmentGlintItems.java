package de.cosmohdx.griefergames.feature.itempreview;

public final class EnchantmentGlintItems {

  private EnchantmentGlintItems() {
  }

  public static boolean applies(String path) {
    if (path == null || path.isEmpty()) {
      return false;
    }
    int separator = path.indexOf(':');
    String id = separator >= 0 ? path.substring(separator + 1) : path;
    if (id.equals("skull") || id.equals("skull_item") || id.endsWith("_skull") || id.endsWith("_head")) {
      return true;
    }
    if (id.equals("banner") || id.endsWith("_banner")) {
      return true;
    }
    return id.equals("chest") || id.equals("trapped_chest") || id.equals("ender_chest");
  }
}
