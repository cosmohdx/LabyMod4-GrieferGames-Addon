package de.cosmohdx.griefergames.feature.itemlist;

import net.labymod.api.client.chat.command.Command;

public class ItemListCommand extends Command {

  public ItemListCommand() {
    super("itemliste", "itemlist");
  }

  @Override
  public boolean execute(String prefix, String[] arguments) {
    ItemListActivity.open();
    return true;
  }
}
