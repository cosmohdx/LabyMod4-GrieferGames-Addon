package de.cosmohdx.griefergames.feature.chat.search;

import net.labymod.api.client.chat.command.Command;

public class ChatSearchCommand extends Command {

  public ChatSearchCommand() {
    super("chatsuche", "chatsearch");
  }

  @Override
  public boolean execute(String prefix, String[] arguments) {
    ChatSearchActivity.open();
    return true;
  }
}
