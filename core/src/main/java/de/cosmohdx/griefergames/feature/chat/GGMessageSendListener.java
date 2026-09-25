package de.cosmohdx.griefergames.feature.chat;

import de.cosmohdx.griefergames.GrieferGames;
import de.cosmohdx.griefergames.feature.automation.ChatColor;
import de.cosmohdx.griefergames.core.SubServerType;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.NamedTextColor;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.chat.ChatMessageSendEvent;
import net.labymod.api.util.I18n;

import java.awt.*;

public class GGMessageSendListener {

    private final GrieferGames griefergames;
    private String lastMessage = "";

    public GGMessageSendListener(GrieferGames griefergames) {
        this.griefergames = griefergames;
    }

    @Subscribe
    public void onSend(ChatMessageSendEvent event) {
        if (!griefergames.state().isOnGrieferGames()) {
            return;
        }
        String msg = event.getMessage();

          if (griefergames.configuration().chat().preventCommandFailure()) {
              if (msg.startsWith("7") && !msg.equalsIgnoreCase(lastMessage)) {
                  griefergames.displayAddonMessage(Component.text(I18n.translate(griefergames.namespace() + ".messages.commandFailure"), NamedTextColor.RED));
                  lastMessage = msg;
                  event.setCancelled(true);
              } else {
                  lastMessage = "";
              }
          }

        if (griefergames.state().getSubServerType() == SubServerType.REGULAR) {
            ChatColor autoColor = griefergames.configuration().automations().autoColor();
            if (autoColor != ChatColor.NONE && !msg.startsWith("/") && !msg.startsWith(".") && !msg.startsWith("-")) {
                if (!msg.startsWith("&" + autoColor.getColorCode()))
                    event.changeMessage("&" + autoColor.getColorCode() + msg);
            }
        }

        if (griefergames.state().getSubServerType() == SubServerType.CLOUD) {
            if (griefergames.configuration().chat().correctCommandCapitalisation()) {
                if (msg.startsWith("/")) {
                    String[] parts = msg.split(" ");
                    String newMsg = parts[0].toLowerCase() + msg.replace(parts[0], "");
                    event.changeMessage(newMsg);
                }
            }
            if (griefergames.configuration().automations().autoColorCloud()) {
                if (!msg.startsWith("/")) {
                    Color color = new Color(griefergames.configuration().automations().autoColorCloudColor());
                    String hexFormat = String.format("&#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
                    if (!msg.startsWith(hexFormat)) event.changeMessage(hexFormat + msg);
                }
            }
            if (griefergames.configuration().automations().colorGradientCloud()) {
                String colorized = griefergames.helper().addGradiant(msg, "#");
                if (!colorized.equals(msg)) {
                    event.changeMessage(colorized);
                }
            }
        }
    }
}