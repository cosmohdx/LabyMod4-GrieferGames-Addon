package de.cosmohdx.griefergames.feature.chat;

import de.cosmohdx.griefergames.GrieferGames;
import de.cosmohdx.griefergames.feature.chat.SecondChatIndicatorGate.Action;
import java.util.ArrayList;
import java.util.List;
import net.labymod.api.Laby;
import net.labymod.api.client.chat.ChatMessage;
import net.labymod.api.client.chat.advanced.IngameChatTab;
import net.labymod.api.client.chat.filter.ChatFilter;
import net.labymod.api.client.chat.filter.FilterChatService;
import net.labymod.api.configuration.labymod.chat.AdvancedChatMessage;
import net.labymod.api.event.Priority;
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

  /**
   * Runs after LabyMod's filter service ({@link Priority#LATE}). That service hides a line on a
   * filter-less tab whenever another tab matches it, which would drop moved messages and skip the
   * unread counter. Filters configured on the second chat itself still hide the line.
   */
  @Subscribe(Priority.LATEST)
  public void onMessageCheckChat(AdvancedChatTabMessageEvent event) {
    IngameChatTab secondChat = griefergames.state().getSecondChat();
    boolean onSecondChat = secondChat != null && event.tab().equals(secondChat);
    boolean indicators = griefergames.configuration().chat().useChatIndicators();
    if (!onSecondChat || !indicators) {
      return;
    }
    OwnFilterMatch ownFilters = this.matchOwnFilters(
        event.tab().config().filters().get(),
        event.message().chatMessage()
    );
    Action action = SecondChatIndicatorGate.decide(
        true,
        true,
        event.message().metadata().has(griefergames.namespace()),
        ownFilters.hasFilters(),
        ownFilters.matches(),
        ownFilters.hides()
    );
    if (action == Action.HIDE) {
      event.setCancelled(true);
    } else if (action == Action.SHOW) {
      event.setCancelled(false);
    }
  }

  private OwnFilterMatch matchOwnFilters(List<ChatFilter> filters, ChatMessage message) {
    if (filters == null || filters.isEmpty() || message == null) {
      return OwnFilterMatch.NONE;
    }
    FilterChatService service = Laby.references().filterChatService();
    List<ChatFilter> remaining = new ArrayList<>(filters);
    boolean matched = false;
    while (!remaining.isEmpty()) {
      ChatFilter match = service.filter(remaining, message);
      if (match == null || !remaining.remove(match)) {
        break;
      }
      matched = true;
      if (Boolean.TRUE.equals(match.shouldHideMessage().get())) {
        return OwnFilterMatch.HIDE;
      }
    }
    return matched ? OwnFilterMatch.MATCH : OwnFilterMatch.MISS;
  }

  private enum OwnFilterMatch {
    NONE(false, false, false),
    MATCH(true, true, false),
    MISS(true, false, false),
    HIDE(true, true, true);

    private final boolean hasFilters;
    private final boolean matches;
    private final boolean hides;

    OwnFilterMatch(boolean hasFilters, boolean matches, boolean hides) {
      this.hasFilters = hasFilters;
      this.matches = matches;
      this.hides = hides;
    }

    boolean hasFilters() {
      return this.hasFilters;
    }

    boolean matches() {
      return this.matches;
    }

    boolean hides() {
      return this.hides;
    }
  }
}
