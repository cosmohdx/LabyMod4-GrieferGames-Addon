package de.cosmohdx.griefergames.feature.chat;

import de.cosmohdx.griefergames.GrieferGames;
import de.cosmohdx.griefergames.feature.chat.GGChatProcessEvent;
import de.cosmohdx.griefergames.core.SubServerType;
import net.labymod.api.Laby;
import net.labymod.api.client.chat.advanced.IngameChatTab;
import net.labymod.api.configuration.labymod.chat.AdvancedChatMessage;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.chat.ChatReceiveEvent;
import net.labymod.api.event.client.chat.advanced.AdvancedChatTabMessageEvent;
import net.labymod.api.util.Color;

import static de.cosmohdx.griefergames.Constants.CHAT_METADATA_CUSTOM_BACKGROUND;

public class GGMessageReceiveListener {
  private final GrieferGames griefergames;

  public GGMessageReceiveListener(GrieferGames griefergames) {
    this.griefergames = griefergames;
  }

  @Subscribe
  public void onMessage(ChatReceiveEvent event) {
    if(!griefergames.state().isOnGrieferGames()) return;
    //System.out.println(GsonComponentSerializer.gson().serialize(event.message()));

    GGChatProcessEvent processEvent = new GGChatProcessEvent(event.chatMessage());
    Laby.labyAPI().eventBus().fire(processEvent);
    if(processEvent.isCancelled()) {
      event.setCancelled(true);
    } else if(processEvent.isSecondChat()) {
      griefergames.helper().displayInSecondChat(AdvancedChatMessage.chat(processEvent.getMessage()));
      if(!processEvent.isKeepInRegularChat()) event.setCancelled(true);
    }

    if(griefergames.state().getSubServerType() == SubServerType.REGULAR) {
      if (event.chatMessage().getPlainText().equals("[Switcher] Daten heruntergeladen!")) {
        if(griefergames.configuration().automations().boosterConfig().loadBoostersOnJoin()) {
          griefergames.state().setHideBoosterMenu(true);
          griefergames.sendMessage("/booster");
        }
      }
    }
  }

  @Subscribe
  public void onTabMessage(AdvancedChatTabMessageEvent event) {
    if(event.message().metadata().has(CHAT_METADATA_CUSTOM_BACKGROUND)) {
      event.message().metadata().set(IngameChatTab.CUSTOM_BACKGROUND,
        ((Color) event.message().chatMessage().metadata().get(CHAT_METADATA_CUSTOM_BACKGROUND)).get());
    }else if(event.message().chatMessage().metadata().has(CHAT_METADATA_CUSTOM_BACKGROUND)) {
      event.message().metadata().set(IngameChatTab.CUSTOM_BACKGROUND,
        ((Color) event.message().chatMessage().metadata().get(CHAT_METADATA_CUSTOM_BACKGROUND)).get());
    }
  }

  @Subscribe
  public void onMessageCheckChat(AdvancedChatTabMessageEvent event) {
    if(GrieferGames.get().state().getSecondChat() == null || !event.tab().equals(GrieferGames.get().state().getSecondChat())) {
      return;
    }
    if(!griefergames.configuration().chatConfig().tabConfig().isUseChatIndicators()) {
      return;
    }
    if(!event.message().metadata().has(griefergames.namespace())) {
      event.setCancelled(true);
    }else{
      event.setCancelled(false);
    }
  }
}
