package de.cosmohdx.griefergames.feature.wiki;

import net.labymod.api.client.chat.command.Command;

/** Opens links embedded in wiki article text within the active wiki screen. */
public final class WikiLinkCommand extends Command {
  public WikiLinkCommand() { super("ggwiki"); }

  @Override
  public boolean execute(String prefix, String[] arguments) {
    if (arguments.length > 0) WikiActivity.navigate(arguments[0]);
    return true;
  }
}
