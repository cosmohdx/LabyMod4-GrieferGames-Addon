package de.cosmohdx.griefergames.v1_8_9;

import de.cosmohdx.griefergames.core.GrieferGamesController;
import de.cosmohdx.griefergames.core.LoadedPlayerView;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
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

  @Override
  public List<LoadedPlayerView> loadedPlayersWithin(double radius) {
    Minecraft minecraft = Minecraft.getMinecraft();
    if (minecraft.theWorld == null || minecraft.thePlayer == null || radius <= 0.0D) {
      return List.of();
    }
    Entity view = minecraft.getRenderViewEntity() != null ? minecraft.getRenderViewEntity() : minecraft.thePlayer;
    UUID selfId = minecraft.thePlayer.getUniqueID();
    UUID viewId = view instanceof EntityPlayer viewed ? viewed.getUniqueID() : selfId;
    double radiusSquared = radius * radius;
    List<LoadedPlayerView> loaded = new ArrayList<>();
    for (EntityPlayer player : minecraft.theWorld.playerEntities) {
      if (player == null) {
        continue;
      }
      UUID id = player.getUniqueID();
      if (id == null || id.equals(selfId) || id.equals(viewId)) {
        continue;
      }
      double dx = player.posX - view.posX;
      double dy = player.posY - view.posY;
      double dz = player.posZ - view.posZ;
      double distanceSquared = dx * dx + dy * dy + dz * dz;
      if (distanceSquared > radiusSquared) {
        continue;
      }
      String name = player.getName() == null ? "" : player.getName();
      loaded.add(new LoadedPlayerView(
          id,
          name,
          Math.sqrt(distanceSquared),
          player.isInvisible(),
          player.isSpectator(),
          clearView(view, player)));
    }
    return loaded;
  }

  private static boolean clearView(Entity view, Entity target) {
    Vec3 from = new Vec3(view.posX, view.posY + view.getEyeHeight(), view.posZ);
    Vec3 head = new Vec3(target.posX, target.posY + target.getEyeHeight(), target.posZ);
    Vec3 body = new Vec3(target.posX, target.posY + (target.height / 2.0D), target.posZ);
    return view.worldObj.rayTraceBlocks(from, head) == null
        || view.worldObj.rayTraceBlocks(from, body) == null;
  }

}
