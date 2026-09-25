package de.cosmohdx.griefergames.feature.itemlist;

import de.cosmohdx.griefergames.GrieferGames;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.resources.ResourceLocation;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.gui.screen.IngameMenuInitializeEvent;
import net.labymod.api.models.OperatingSystem;

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
        Component.translatable("griefergames.settings.openItemList.name"),
        Icon.texture(ResourceLocation.create(namespace, "textures/itemlist.png")),
        ItemListActivity::open
    );
    event.addLeftButton(
        Component.translatable("griefergames.settings.openWiki.name"),
        Icon.texture(ResourceLocation.create(namespace, "textures/wiki.png")),
        () -> OperatingSystem.getPlatform().openUrl("https://wiki.griefergames.net/")
    );
  }
}
