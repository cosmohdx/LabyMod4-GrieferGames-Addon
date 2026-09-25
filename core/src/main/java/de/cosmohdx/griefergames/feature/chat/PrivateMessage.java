package de.cosmohdx.griefergames.feature.chat;

import de.cosmohdx.griefergames.GrieferGames;
import de.cosmohdx.griefergames.feature.chat.GGChatProcessEvent;
import de.cosmohdx.griefergames.feature.chat.Sounds;
import de.cosmohdx.griefergames.core.SubServerType;
import net.labymod.api.Laby;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.event.ClickEvent;
import net.labymod.api.client.component.event.HoverEvent;
import net.labymod.api.client.component.format.NamedTextColor;
import net.labymod.api.client.resources.ResourceLocation;
import net.labymod.api.event.Subscribe;
import net.labymod.api.util.I18n;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PrivateMessage extends ChatModule {
  private final GrieferGames griefergames;
  private final Pattern privateMessageRegex = Pattern.compile("\\[([A-Za-z\\-\\+]+) \\u2503 (~?\\!?\\w{1,16}) -> mir\\] (.*)$");
  private final Pattern privateMessageRegexCloud = Pattern.compile("\\[([A-Za-z\\-\\+]+) \\u2503 (~?\\!?\\w{1,16}) -> (mir|me)\\] (.*)$");
  private final Pattern privateMessageSentRegex = Pattern.compile("\\[(mir|me) -> ([A-Za-z\\-\\+]+) \\u2503 (~?\\!?\\w{1,16})\\] (.*)$");
  private long lastAfkMessage = 0;
  private final String CHAT_MESSAGE_SEPERATOR = "»";

  public PrivateMessage(GrieferGames griefergames) {
    this.griefergames = griefergames;
  }

  @Subscribe
  public void messageProcessEvent(GGChatProcessEvent event) {
    if (event.isCancelled()) return;
    if (event.getMessage().getPlainText().contains(CHAT_MESSAGE_SEPERATOR)) {
      // Fake MSG message
      return;
    }
    Matcher privateMessage = privateMessageRegexCloud.matcher(griefergames.helper().removeLeadingMiscCodes(event.getMessage().getPlainText()));
    boolean isIncomingPrivateMessage = privateMessage.find();

    // Auto AFK message reply
    if (isIncomingPrivateMessage) {
      String playerName = privateMessage.group(2);
      if (griefergames.configuration().automations().afkConfig().afkMsgReply().get() && griefergames.isAfk() && lastAfkMessage + 1000 <= System.currentTimeMillis()) {
        String message = griefergames.configuration().automations().afkConfig().afkMsgText().get();
        if (!message.isBlank()) {
          griefergames.sendMessage("/msg " + playerName + " " + message);
          lastAfkMessage = System.currentTimeMillis();
        }
      }
    }

    // Incoming private message
    if (isIncomingPrivateMessage) {
      String playerName = privateMessage.group(2);

      if (griefergames.configuration().chatConfig().isClickToReply() && griefergames.isSubServerType(SubServerType.REGULAR)) {
        addReplyAction(event.getMessage().component(), "§6[", "§6 -> ", playerName);
      }

      if (griefergames.configuration().chatConfig().isPrivateChatRight()) {
        event.setSecondChat(true);
      }

      if (griefergames.configuration().chatConfig().getPrivateChatSound() != Sounds.NONE) {
        ResourceLocation resource = ResourceLocation.create("minecraft", griefergames.configuration().chatConfig().getPrivateChatSound().path());
        Laby.labyAPI().minecraft().sounds().playSound(resource, 1f, 1f);
      }
    }

    // Outgoing private message
    Matcher privateMessageSent = privateMessageSentRegex.matcher(griefergames.helper().removeLeadingMiscCodes(event.getMessage().getPlainText()));
    if (privateMessageSent.find()) {
      if (griefergames.configuration().chatConfig().isClickToReply() && griefergames.isSubServerType(SubServerType.REGULAR)) {
        addReplyAction(event.getMessage().component(), "§6 -> ", "§6] ", privateMessageSent.group(3));
      }

      if (griefergames.configuration().chatConfig().isPrivateChatRight()) {
        event.setSecondChat(true);
      }
    }
  }

  /**
   * Adds a reply action to the message sender
   * @param message Message
   * @param startText Start text
   * @param endText End text
   * @param name Name of the player
   */
  private void addReplyAction(Component message, String startText, String endText, String name) {
    int startIndex = -1, endIndex = -1;
    for (int i = 0; i < message.getChildren().size(); i++) {
      Component component = message.getChildren().get(i);
      String formatted = this.getFormattedText(component);

      if (startIndex == -1 && formatted.equals(startText)) {
        startIndex = i + 1;
      }

      if (endIndex == -1 && formatted.equals(endText)) {
        endIndex = i - 1;
      }
    }

    for (int i = startIndex; i <= endIndex; i++) {
      Component component = message.getChildren().get(i);
      component.style(component.style()
        .hoverEvent(HoverEvent.showText(Component.text(I18n.translate(griefergames.namespace() + ".messages.clickToReply"), NamedTextColor.GREEN)))
        .clickEvent(ClickEvent.suggestCommand("/msg " + name + " ")));
    }
  }
}
