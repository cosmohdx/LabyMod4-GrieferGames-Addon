package de.cosmohdx.griefergames.feature.chat;

import net.labymod.api.client.chat.ChatMessage;
import net.labymod.api.event.Cancellable;
import net.labymod.api.event.Event;

public class GGChatProcessEvent implements Event, Cancellable {
  private boolean cancelled = false;
  private boolean secondChat = false, keepInRegularChat = false;
  private ChatMessage message;

  public GGChatProcessEvent(ChatMessage message) {
    this.message = message;
  }

  public ChatMessage getMessage() {
    return message;
  }

  @Override
  public boolean isCancelled() {
    return cancelled;
  }
  @Override
  public void setCancelled(boolean cancelled) {
    this.cancelled = cancelled;
  }

  public boolean isSecondChat() {
    return secondChat;
  }
  public void setSecondChat(boolean secondChat) {
    this.secondChat = secondChat;
  }

  public boolean isKeepInRegularChat() {
    return keepInRegularChat;
  }

  public void setSecondChat(boolean secondChat, boolean keepInRegularChat) {
    this.secondChat = secondChat;
    this.keepInRegularChat = keepInRegularChat;
  }
}
