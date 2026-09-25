package de.cosmohdx.griefergames.v1_21;

import de.cosmohdx.griefergames.core.GrieferGamesController;
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
    Screen screen = Minecraft.getInstance().screen;
    if (screen instanceof AbstractContainerScreen) {
      if (screen.getTitle().getString().equals("§6Booster - Übersicht")) {
        Minecraft.getInstance().player.closeContainer();
        return true;
      }
    }
    return false;
  }
}
