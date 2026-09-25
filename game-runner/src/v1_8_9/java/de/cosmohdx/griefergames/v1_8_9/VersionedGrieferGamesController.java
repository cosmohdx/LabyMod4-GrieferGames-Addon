package de.cosmohdx.griefergames.v1_8_9;

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
    if (Minecraft.getMinecraft().thePlayer == null) {
      return false;
    }
    return Minecraft.getMinecraft().thePlayer.capabilities.allowFlying;
  }

  @Override
  public boolean hideBoosterMenu() {
    if (Minecraft.getMinecraft().thePlayer == null) {
      return false;
    }
    Container cont = Minecraft.getMinecraft().thePlayer.openContainer;
    if (cont instanceof ContainerChest) {
      ContainerChest chest = (ContainerChest) cont;
      IInventory inv = chest.getLowerChestInventory();
      if (inv.getName().equals("§6Booster - Übersicht")) {
        Minecraft.getMinecraft().thePlayer.closeScreen();
        return true;
      }
    }
    return false;
  }
}
