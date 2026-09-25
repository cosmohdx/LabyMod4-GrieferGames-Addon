package de.cosmohdx.griefergames.feature.itempreview;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class EnchantmentGlintItemsTest {

  @Test
  void matchesSkullsBannersAndChests() {
    assertTrue(EnchantmentGlintItems.applies("skull"));
    assertTrue(EnchantmentGlintItems.applies("minecraft:player_head"));
    assertTrue(EnchantmentGlintItems.applies("wither_skeleton_skull"));
    assertTrue(EnchantmentGlintItems.applies("dragon_head"));
    assertTrue(EnchantmentGlintItems.applies("white_banner"));
    assertTrue(EnchantmentGlintItems.applies("banner"));
    assertTrue(EnchantmentGlintItems.applies("chest"));
    assertTrue(EnchantmentGlintItems.applies("trapped_chest"));
    assertTrue(EnchantmentGlintItems.applies("ender_chest"));
  }

  @Test
  void ignoresItemsThatAlreadyRenderTheirOwnGlint() {
    assertFalse(EnchantmentGlintItems.applies("shield"));
    assertFalse(EnchantmentGlintItems.applies("diamond_chestplate"));
    assertFalse(EnchantmentGlintItems.applies("shulker_box"));
    assertFalse(EnchantmentGlintItems.applies(null));
    assertFalse(EnchantmentGlintItems.applies(""));
  }
}
