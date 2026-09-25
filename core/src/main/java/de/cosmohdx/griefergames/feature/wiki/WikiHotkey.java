package de.cosmohdx.griefergames.feature.wiki;

import de.cosmohdx.griefergames.GrieferGames;
import net.labymod.api.Laby;
import net.labymod.api.client.gui.screen.key.Key;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.input.KeyEvent;

/** Opens the wiki only from the game view, so chat and other screens keep their keys. */
public final class WikiHotkey {
  private final GrieferGames addon;

  public WikiHotkey(GrieferGames addon) {
    this.addon = addon;
  }

  @Subscribe
  public void onKey(KeyEvent event) {
    if (event.state() != KeyEvent.State.PRESS || !this.addon.configuration().enabled().get()) return;
    Key assigned = this.addon.configuration().wikiKey().get();
    if (assigned == null || !assigned.equals(event.key())) return;
    if (Laby.labyAPI().minecraft().minecraftWindow().isScreenOpened()) return;
    Laby.labyAPI().minecraft().minecraftWindow().displayScreen(new WikiActivity());
  }
}
