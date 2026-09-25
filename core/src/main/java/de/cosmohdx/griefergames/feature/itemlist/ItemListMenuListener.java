package de.cosmohdx.griefergames.feature.itemlist;

import de.cosmohdx.griefergames.GrieferGames;
import de.cosmohdx.griefergames.feature.wiki.WikiActivity;
import net.labymod.api.Laby;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.gui.screen.key.Key;
import net.labymod.api.client.resources.ResourceLocation;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.gui.screen.IngameMenuInitializeEvent;
import net.labymod.api.event.client.input.KeyEvent;
import net.labymod.api.event.client.input.KeyEvent.State;

public class ItemListMenuListener {

  private final GrieferGames griefergames;

  public ItemListMenuListener(GrieferGames griefergames) {
    this.griefergames = griefergames;
  }

  @Subscribe
  public void onIngameMenu(IngameMenuInitializeEvent event) {
    if (!this.griefergames.configuration().enabled().get()) {
      return;
    }
    String namespace = this.griefergames.namespace();
    event.addLeftButton(
        Component.translatable("griefergames.settings.itemList.name"),
        Icon.texture(ResourceLocation.create(namespace, "textures/itemlist.png")),
        ItemListActivity::open
    );
    event.addLeftButton(
        Component.translatable("griefergames.settings.wiki.name"),
        Icon.texture(ResourceLocation.create(namespace, "textures/wiki.png")),
        () -> Laby.labyAPI().minecraft().minecraftWindow().displayScreen(new WikiActivity())
    );
  }

  @Subscribe
  public void onKey(KeyEvent event) {
    if (event.state() != State.PRESS || !this.griefergames.configuration().enabled().get()) {
      return;
    }
    if (Laby.labyAPI().minecraft().minecraftWindow().isScreenOpened()) {
      return;
    }
    Key bound = this.griefergames.configuration().itemList().key();
    if (bound.equals(Key.NONE) || !bound.equals(event.key())) {
      return;
    }
    event.setCancelled(true);
    ItemListActivity.open();
  }
}
