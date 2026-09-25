package de.cosmohdx.griefergames.core;

import de.cosmohdx.griefergames.GrieferGames;
import net.labymod.api.Laby;
import net.labymod.api.client.chat.command.Command;
import net.labymod.api.client.chat.command.SubCommand;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.NamedTextColor;
import net.labymod.api.client.component.serializer.gson.GsonComponentSerializer;
import java.util.Arrays;

public class GGMessageCommand extends Command {
  private final GrieferGames griefergames;

  public GGMessageCommand(GrieferGames griefergames) {
    super("ggmessage");
    this.griefergames = griefergames;

    this.withSubCommand(new SubCommand("text") {
      @Override
      public boolean execute(String prefix, String[] arguments) {
        if(arguments.length > 0) {
          String message = String.join(" ", arguments).replace("&", "§");
          griefergames.displayMessage(Component.text(message));
        } else {
          griefergames.displayAddonMessage(Component.text("Usage: /ggmessage text <colored_message>", NamedTextColor.RED));
        }
        return true;
      }
    });

    this.withSubCommand(new SubCommand("json") {
      @Override
      public boolean execute(String prefix, String[] arguments) {
        if(arguments.length > 0) {
          String message = String.join(" ", arguments);
          try {
            griefergames.displayMessage(GsonComponentSerializer.gson().deserialize(message));
          } catch(Exception e) {
            e.printStackTrace();
            griefergames.displayAddonMessage(Component.text("Invalide json.", NamedTextColor.RED));
          }
        } else {
          griefergames.displayAddonMessage(Component.text("Usage: /ggmessage json <json_message>", NamedTextColor.RED));
        }
        return true;
      }
    });
  }

  @Override
  public boolean execute(String prefix, String[] arguments) {
    griefergames.displayAddonMessage(Component.text("Usage: /ggmessage text <colored_message>", NamedTextColor.RED));
    griefergames.displayAddonMessage(Component.text("Usage: /ggmessage json <json_message>", NamedTextColor.RED));
    return true;
  }
}
