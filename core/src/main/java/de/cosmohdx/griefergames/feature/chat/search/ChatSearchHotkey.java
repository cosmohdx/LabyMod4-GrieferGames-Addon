package de.cosmohdx.griefergames.feature.chat.search;

import de.cosmohdx.griefergames.GrieferGames;
import net.labymod.api.Laby;
import net.labymod.api.client.gui.screen.NamedScreen;
import net.labymod.api.client.gui.screen.key.Key;
import net.labymod.api.client.gui.window.Window;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.input.KeyEvent;

/** Opens chat search from the game view, and with Ctrl+F while the chat screen is open. */
public final class ChatSearchHotkey {

  private final GrieferGames addon;

  public ChatSearchHotkey(GrieferGames addon) {
    this.addon = addon;
  }

  @Subscribe
  public void onKey(KeyEvent event) {
    if (event.state() != KeyEvent.State.PRESS || !this.addon.configuration().enabled().get()) {
      return;
    }
    if (!this.addon.configuration().chat().isEnabled()
        || !this.addon.configuration().chat().chatSearch().isEnabled()) {
      return;
    }
    Window window = Laby.labyAPI().minecraft().minecraftWindow();
    if (this.isChatFind(event, window)) {
      event.setCancelled(true);
      ChatSearchActivity.open();
      return;
    }
    Key assigned = this.addon.configuration().chat().chatSearch().key();
    if (assigned.equals(Key.NONE) || !assigned.equals(event.key())) {
      return;
    }
    if (window.isScreenOpened()) {
      return;
    }
    ChatSearchActivity.open();
  }

  private boolean isChatFind(KeyEvent event, Window window) {
    if (!event.key().equals(Key.F)) {
      return false;
    }
    if (!window.isScreenDisplayed(NamedScreen.CHAT_INPUT)
        && !window.isScreenDisplayed(NamedScreen.CHAT_INPUT_IN_BED)) {
      return false;
    }
    return Laby.labyAPI().minecraft().isKeyPressed(Key.L_CONTROL)
        || Laby.labyAPI().minecraft().isKeyPressed(Key.R_CONTROL);
  }
}
