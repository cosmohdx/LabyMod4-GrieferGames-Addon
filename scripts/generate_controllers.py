#!/usr/bin/env python3
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]

LEGACY_18 = r'''package de.cosmohdx.griefergames.PKG;

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
'''

LEGACY_112 = r'''package de.cosmohdx.griefergames.PKG;

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
}
'''

ABILITIES = r'''package de.cosmohdx.griefergames.PKG;

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
    return Minecraft.getInstance().player.abilities.mayfly;
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
}
'''

CONTAINER = r'''package de.cosmohdx.griefergames.PKG;

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
}
'''

ABSTRACT26 = r'''package de.cosmohdx.griefergames.PKG;

import de.cosmohdx.griefergames.core.GrieferGamesController;
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
}
'''

ABSTRACT = r'''package de.cosmohdx.griefergames.PKG;

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
'''

VERSIONS = [
    ("1.8.9", LEGACY_18),
    ("1.12.2", LEGACY_112),
    ("1.16.5", ABILITIES),
    ("1.17.1", CONTAINER),
    ("1.18.2", CONTAINER),
    ("1.19.4", CONTAINER),
    ("1.20.1", CONTAINER),
    ("1.20.4", CONTAINER),
    ("1.20.6", CONTAINER),
    ("1.21", ABSTRACT),
    ("1.21.1", ABSTRACT),
    ("1.21.3", ABSTRACT),
    ("1.21.4", ABSTRACT),
    ("1.21.5", ABSTRACT),
    ("1.21.8", ABSTRACT),
    ("1.21.10", ABSTRACT),
    ("1.21.11", ABSTRACT),
    ("26.1", ABSTRACT26),
    ("26.1.1", ABSTRACT26),
    ("26.1.2", ABSTRACT26),
    ("26.2", ABSTRACT26),
    ("26.3", ABSTRACT26),
]


def main():
    for version, template in VERSIONS:
        pkg = "v" + version.replace(".", "_")
        dest = ROOT / f"game-runner/src/{pkg}/java/de/cosmohdx/griefergames/{pkg}"
        dest.mkdir(parents=True, exist_ok=True)
        (dest / "VersionedGrieferGamesController.java").write_text(
            template.replace("PKG", pkg), encoding="utf-8"
        )
        print("wrote", pkg)


if __name__ == "__main__":
    main()
