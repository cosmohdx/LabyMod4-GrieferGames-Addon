package de.cosmohdx.griefergames.feature.itemlist;

import de.cosmohdx.griefergames.GrieferGames;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.resources.ResourceLocation;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.gui.screen.IngameMenuInitializeEvent;

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
    event.addLeftButton(
        Component.translatable("griefergames.settings.openItemList.name"),
        Icon.texture(ResourceLocation.create(this.griefergames.namespace(), "textures/icon.png")),
        ItemListActivity::open
    );
  }
}
