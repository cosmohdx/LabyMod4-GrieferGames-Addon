package de.cosmohdx.griefergames.feature.itemlist;

import net.labymod.api.Laby;
import net.labymod.api.client.chat.command.Command;

public class ItemListCommand extends Command {

  public ItemListCommand() {
    super("itemliste", "itemlist");
  }

  @Override
  public boolean execute(String prefix, String[] arguments) {
    Laby.labyAPI().minecraft().executeNextTick(() ->
        Laby.labyAPI().minecraft().minecraftWindow().displayScreen(new ItemListActivity())
    );
    return true;
  }
}
