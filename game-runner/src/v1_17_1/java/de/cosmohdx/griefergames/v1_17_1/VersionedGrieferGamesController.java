package de.cosmohdx.griefergames.v1_17_1;

import de.cosmohdx.griefergames.core.GrieferGamesController;
import net.labymod.api.models.Implements;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@Implements(GrieferGamesController.class)
public class VersionedGrieferGamesController extends GrieferGamesController {

  @Inject
  public VersionedGrieferGamesController() {}

  @Override
  public boolean playerAllowedFlying() {
    if (Minecraft.getInstance().player == null) {
      return false;
    }
    return Minecraft.getInstance().player.getAbilities().mayfly;
  }

  @Override
  public boolean hideBoosterMenu() {
    if (Minecraft.getInstance().player == null) {
      return false;
    }
    Screen screen = Minecraft.getInstance().screen;
    if (screen instanceof ContainerScreen) {
      if (screen.getTitle().getString().equals("§6Booster - Übersicht")) {
        Minecraft.getInstance().player.closeContainer();
        return true;
      }
    }
    return false;
  }


  @Override
  public int[] filledMapPixels(int mapId) {
    if (Minecraft.getInstance().level == null) {
      return null;
    }
    net.minecraft.world.level.saveddata.maps.MapItemSavedData data = net.minecraft.world.item.MapItem.getSavedData(mapId, Minecraft.getInstance().level);
    if (data == null) {
      return null;
    }
    return de.cosmohdx.griefergames.feature.itempreview.MapPixels.toArgb(data.colors, packed -> {
      net.minecraft.world.level.material.MaterialColor[] palette = net.minecraft.world.level.material.MaterialColor.MATERIAL_COLORS;
      int index = packed >> 2;
      if (index < 0 || index >= palette.length || palette[index] == null) {
        return 0;
      }
      return palette[index].calculateRGBColor(packed & 3);
    });
  }

  @Override
  public boolean patchSpecialItemEnchantmentGlint() {
    return true;
  }
}
