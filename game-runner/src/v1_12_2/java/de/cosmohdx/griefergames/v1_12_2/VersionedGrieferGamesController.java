package de.cosmohdx.griefergames.v1_12_2;

import de.cosmohdx.griefergames.core.GrieferGamesController;
import net.labymod.api.models.Implements;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@Implements(GrieferGamesController.class)
public class VersionedGrieferGamesController extends GrieferGamesController {

  @Inject
  public VersionedGrieferGamesController() {}

  @Override
  public boolean playerAllowedFlying() {
    if (Minecraft.getMinecraft().player == null) {
      return false;
    }
    return Minecraft.getMinecraft().player.capabilities.allowFlying;
  }

  @Override
  public boolean hideBoosterMenu() {
    if (Minecraft.getMinecraft().player == null) {
      return false;
    }
    Container cont = Minecraft.getMinecraft().player.openContainer;
    if (cont instanceof ContainerChest) {
      ContainerChest chest = (ContainerChest) cont;
      IInventory inv = chest.getLowerChestInventory();
      if (inv.getName().equals("§6Booster - Übersicht")) {
        Minecraft.getMinecraft().player.closeScreen();
        return true;
      }
    }
    return false;
  }


  @Override
  public int[] filledMapPixels(int mapId) {
    if (Minecraft.getMinecraft().world == null) {
      return null;
    }
    net.minecraft.world.storage.MapData data = net.minecraft.item.ItemMap.loadMapData(mapId, Minecraft.getMinecraft().world);
    if (data == null) {
      return null;
    }
    return de.cosmohdx.griefergames.feature.itempreview.MapPixels.toArgb(data.colors, packed -> {
      net.minecraft.block.material.MapColor[] palette = net.minecraft.block.material.MapColor.COLORS;
      int index = packed >> 2;
      if (index < 0 || index >= palette.length || palette[index] == null) {
        return 0;
      }
      return palette[index].getMapColor(packed & 3);
    });
  }

  @Override
  public boolean patchSpecialItemEnchantmentGlint() {
    return true;
  }
}
