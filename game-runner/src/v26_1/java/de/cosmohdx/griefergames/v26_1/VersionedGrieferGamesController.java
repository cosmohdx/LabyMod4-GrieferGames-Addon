package de.cosmohdx.griefergames.v26_1;

import de.cosmohdx.griefergames.core.GrieferGamesController;
import de.cosmohdx.griefergames.core.LoadedPlayerView;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.labymod.api.Laby;
import net.labymod.api.models.Implements;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
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
    Object rawScreen = Laby.labyAPI().minecraft().minecraftWindow().getCurrentVersionedScreen();
    if (rawScreen instanceof AbstractContainerScreen) {
      Screen screen = (Screen) rawScreen;
      if (screen.getTitle().getString().equals("§6Booster - Übersicht")) {
        Minecraft.getInstance().player.closeContainer();
        return true;
      }
    }
    return false;
  }

  @Override
  public List<LoadedPlayerView> loadedPlayersWithin(double radius) {
    Minecraft minecraft = Minecraft.getInstance();
    if (minecraft.player == null || minecraft.level == null || radius <= 0.0D) {
      return List.of();
    }
    Entity view = minecraft.getCameraEntity() != null ? minecraft.getCameraEntity() : minecraft.player;
    UUID selfId = minecraft.player.getUUID();
    UUID viewId = view instanceof Player viewed ? viewed.getUUID() : selfId;
    double radiusSquared = radius * radius;
    List<LoadedPlayerView> loaded = new ArrayList<>();
    for (var player : minecraft.level.players()) {
      if (player == null) {
        continue;
      }
      UUID id = player.getUUID();
      if (id == null || id.equals(selfId) || id.equals(viewId)) {
        continue;
      }
      double dx = player.getX() - view.getX();
      double dy = player.getY() - view.getY();
      double dz = player.getZ() - view.getZ();
      double distanceSquared = dx * dx + dy * dy + dz * dz;
      if (distanceSquared > radiusSquared) {
        continue;
      }
      String name = player.getScoreboardName() == null ? "" : player.getScoreboardName();
      loaded.add(new LoadedPlayerView(
          id,
          name,
          Math.sqrt(distanceSquared),
          player.isInvisible(),
          player.isSpectator(),
          clearView(minecraft, view, player)));
    }
    return loaded;
  }

  private static boolean clearView(Minecraft minecraft, Entity view, Entity target) {
    Vec3 from = new Vec3(view.getX(), view.getEyeY(), view.getZ());
    Vec3 head = new Vec3(target.getX(), target.getEyeY(), target.getZ());
    Vec3 body = new Vec3(target.getX(), target.getY() + (target.getBbHeight() / 2.0D), target.getZ());
    return missed(minecraft, view, from, head) || missed(minecraft, view, from, body);
  }

  private static boolean missed(Minecraft minecraft, Entity view, Vec3 from, Vec3 to) {
    HitResult hit = minecraft.level.clip(new ClipContext(
        from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, view));
    return hit.getType() == HitResult.Type.MISS;
  }

}
