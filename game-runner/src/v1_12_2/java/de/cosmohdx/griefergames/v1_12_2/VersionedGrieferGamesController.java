package de.cosmohdx.griefergames.v1_12_2;

import de.cosmohdx.griefergames.core.GrieferGamesController;
import de.cosmohdx.griefergames.core.LoadedPlayerView;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
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
  public List<LoadedPlayerView> loadedPlayersWithin(double radius) {
    Minecraft minecraft = Minecraft.getMinecraft();
    if (minecraft.world == null || minecraft.player == null || radius <= 0.0D) {
      return List.of();
    }
    Entity view = minecraft.getRenderViewEntity() != null ? minecraft.getRenderViewEntity() : minecraft.player;
    UUID selfId = minecraft.player.getUniqueID();
    UUID viewId = view instanceof EntityPlayer viewed ? viewed.getUniqueID() : selfId;
    double radiusSquared = radius * radius;
    List<LoadedPlayerView> loaded = new ArrayList<>();
    for (EntityPlayer player : minecraft.world.playerEntities) {
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
    Vec3d from = new Vec3d(view.posX, view.posY + view.getEyeHeight(), view.posZ);
    Vec3d head = new Vec3d(target.posX, target.posY + target.getEyeHeight(), target.posZ);
    Vec3d body = new Vec3d(target.posX, target.posY + (target.height / 2.0D), target.posZ);
    return view.world.rayTraceBlocks(from, head, false, true, false) == null
        || view.world.rayTraceBlocks(from, body, false, true, false) == null;
  }

}
