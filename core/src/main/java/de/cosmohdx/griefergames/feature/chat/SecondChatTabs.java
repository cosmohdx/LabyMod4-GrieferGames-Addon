package de.cosmohdx.griefergames.feature.chat;

import de.cosmohdx.griefergames.GrieferGames;
import de.cosmohdx.griefergames.feature.chat.SecondChatTabSelector.Candidate;
import de.cosmohdx.griefergames.feature.chat.SecondChatTabSelector.Decision;
import de.cosmohdx.griefergames.feature.subserver.GGSubServerChangeEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.labymod.api.Laby;
import net.labymod.api.client.chat.advanced.AdvancedChatController;
import net.labymod.api.client.chat.advanced.IngameChatTab;
import net.labymod.api.client.chat.filter.ChatFilter;
import net.labymod.api.configuration.exception.ConfigurationSaveException;
import net.labymod.api.configuration.labymod.chat.ChatTab;
import net.labymod.api.configuration.labymod.chat.ChatWindow;
import net.labymod.api.configuration.labymod.chat.config.RootChatTabConfig;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.chat.advanced.AdvancedChatReloadEvent;

/**
 * Binds the addon to one LabyMod chat tab and keeps that binding across joins, subserver switches
 * and chat reloads.
 */
public final class SecondChatTabs {

  static final String MARKER = "griefergames.secondChat";
  static final String LEGACY_FILTER_TAG = "§chzgwefegsdrutjugiuteghuzazghwu";

  private final GrieferGames griefergames;
  private boolean syncing;

  public SecondChatTabs(GrieferGames griefergames) {
    this.griefergames = griefergames;
    GrieferGamesChatConfig chat = griefergames.configuration().chat();
    SecondChatConfig secondChat = chat.secondChat();
    griefergames.configuration().enabled().addChangeListener(() -> this.sync());
    chat.enabled().addChangeListener(() -> this.sync());
    secondChat.enabled().addChangeListener(() -> this.sync());
    secondChat.createProperty().addChangeListener(() -> this.sync());
    secondChat.manageFiltersProperty().addChangeListener(() -> this.sync());
    secondChat.chatIndicatorsProperty().addChangeListener(() -> this.sync());
    secondChat.chatTabNameProperty().addChangeListener(
        (property, oldValue, newValue) -> this.sync(oldValue)
    );
  }

  @Subscribe
  public void onAdvancedChatReload(AdvancedChatReloadEvent event) {
    this.sync();
  }

  @Subscribe
  public void onSubServerChange(GGSubServerChangeEvent event) {
    this.sync();
  }

  public void sync() {
    this.sync(null);
  }

  private void sync(String previousName) {
    if (this.syncing) {
      return;
    }
    this.syncing = true;
    try {
      this.syncUnlocked(previousName);
    } finally {
      this.syncing = false;
    }
  }

  private void syncUnlocked(String previousName) {
    GrieferGamesChatConfig chat = this.griefergames.configuration().chat();
    SecondChatConfig secondChat = chat.secondChat();
    if (!this.griefergames.configuration().enabled().get()
        || !chat.isEnabled()
        || !secondChat.isEnabled()) {
      this.griefergames.state().setSecondChat(null);
      return;
    }

    AdvancedChatController controller = Laby.references().advancedChatController();
    List<IngameChatTab> tabs = this.ingameTabs(controller);
    List<Candidate> candidates = new ArrayList<>(tabs.size());
    for (IngameChatTab tab : tabs) {
      candidates.add(this.candidate(tab));
    }

    String configuredName = secondChat.chatTabName();
    boolean create = this.griefergames.state().isOnGrieferGames()
        && chat.createSecondChat()
        && configuredName != null
        && !configuredName.isBlank();
    Decision decision = SecondChatTabSelector.select(
        candidates,
        secondChat.tabId(),
        configuredName,
        previousName,
        create
    );

    IngameChatTab tab = switch (decision.action()) {
      case REUSE -> this.collapseSameConfig(tabs.get(decision.index()));
      case CREATE -> this.createTab(controller, configuredName);
      case NONE -> null;
    };
    if (tab != null) {
      boolean filtersChanged = this.applyFilters(tab);
      this.remember(tab, filtersChanged);
    }
    this.griefergames.state().setSecondChat(tab);
  }

  private List<IngameChatTab> ingameTabs(AdvancedChatController controller) {
    List<IngameChatTab> tabs = new ArrayList<>();
    for (ChatWindow window : controller.getWindows()) {
      for (ChatTab tab : window.getTabs()) {
        if (tab instanceof IngameChatTab ingameChatTab) {
          tabs.add(ingameChatTab);
        }
      }
    }
    return tabs;
  }

  private Candidate candidate(IngameChatTab tab) {
    String storedName = tab.config().name().get();
    return new Candidate(
        tab.rootConfig().getUniqueID().toString(),
        storedName,
        tab.rootConfig().hasConfigMeta(MARKER),
        this.hasLegacyFilter(tab)
    );
  }

  private boolean hasLegacyFilter(IngameChatTab tab) {
    List<ChatFilter> filters = tab.config().filters().get();
    if (filters == null) {
      return false;
    }
    for (ChatFilter filter : filters) {
      if (filter.getIncludedTags().getTags().stream()
          .anyMatch(tag -> LEGACY_FILTER_TAG.equals(tag.getContent()))) {
        return true;
      }
    }
    return false;
  }

  private IngameChatTab createTab(AdvancedChatController controller, String name) {
    RootChatTabConfig config = new RootChatTabConfig(
        RootChatTabConfig.Type.CUSTOM,
        name
    );
    config.configMeta().put(MARKER, "1");

    // A missing secondary window is created from this config and reload() already builds the tab.
    // Calling initializeTab again would add a second runtime tab for the same config.
    ChatWindow window = controller.getOrCreateSecondaryWindow(() -> config);
    IngameChatTab created = this.findById(controller, config.getUniqueID());
    if (created != null) {
      return created;
    }

    boolean persisted = this.containsConfig(window, config.getUniqueID());
    if (!persisted) {
      config.index().set(this.nextIndex(window));
    }
    ChatTab tab = window.initializeTab(config, null, !persisted);
    if (!persisted) {
      window.save();
    }
    this.griefergames.logger().info(
        GrieferGames.LOG_PREFIX + "Created second chat tab \"" + name + "\""
    );
    return (IngameChatTab) tab;
  }

  private IngameChatTab findById(AdvancedChatController controller, UUID id) {
    for (IngameChatTab tab : this.ingameTabs(controller)) {
      if (id.equals(tab.rootConfig().getUniqueID())) {
        return tab;
      }
    }
    return null;
  }

  private boolean containsConfig(ChatWindow window, UUID id) {
    for (RootChatTabConfig config : window.config().getTabs()) {
      if (id.equals(config.getUniqueID())) {
        return true;
      }
    }
    return false;
  }

  private int nextIndex(ChatWindow window) {
    int next = 0;
    for (RootChatTabConfig config : window.config().getTabs()) {
      Integer index = config.index().get();
      if (index != null) {
        next = Math.max(next, index + 1);
      }
    }
    return next;
  }

  /**
   * {@code initializeTab} used to be called for a config that reload() had already turned into a
   * tab. Both instances share one config, so removing the extra runtime tab does not delete it.
   */
  private IngameChatTab collapseSameConfig(IngameChatTab keep) {
    RootChatTabConfig config = keep.rootConfig();
    for (ChatWindow window : Laby.references().advancedChatController().getWindows()) {
      window.getTabs().removeIf(tab -> tab != keep && tab.rootConfig() == config);
    }
    return keep;
  }

  private void remember(IngameChatTab tab, boolean dirty) {
    RootChatTabConfig rootConfig = tab.rootConfig();
    if (!rootConfig.hasConfigMeta(MARKER)) {
      rootConfig.configMeta().put(MARKER, "1");
      dirty = true;
    }

    String configuredName = this.griefergames.configuration().chat().secondChat().chatTabName();
    if (configuredName != null && !configuredName.isBlank()) {
      String currentName = tab.config().name().get();
      if (currentName == null || !configuredName.equals(currentName)) {
        tab.config().name().set(configuredName);
        dirty = true;
      }
    }

    if (dirty) {
      tab.window().save();
    }

    String id = rootConfig.getUniqueID().toString();
    SecondChatConfig secondChat = this.griefergames.configuration().chat().secondChat();
    if (id.equalsIgnoreCase(secondChat.tabId())) {
      return;
    }
    secondChat.tabId(id);
    try {
      this.griefergames.saveConfiguration();
    } catch (ConfigurationSaveException exception) {
      this.griefergames.logger().warn(
          GrieferGames.LOG_PREFIX + "Could not store the second chat tab id",
          exception
      );
    }
  }

  private boolean applyFilters(IngameChatTab secondChat) {
    GrieferGamesChatConfig chat = this.griefergames.configuration().chat();
    List<ChatFilter> filters = secondChat.config().filters().get();
    if (filters == null) {
      return false;
    }
    if (chat.useChatIndicators()) {
      if (filters.isEmpty()) {
        return false;
      }
      if (chat.manageSecondChatFilters()) {
        filters.clear();
        return true;
      }
      return filters.removeIf(this::isLegacyFilter);
    }
    if (!chat.manageSecondChatFilters() || !filters.isEmpty()) {
      return false;
    }
    ChatFilter defaultChatFilter = new ChatFilter();
    defaultChatFilter.name().set("GrieferGames-Addon");
    defaultChatFilter.getIncludedTags().add(LEGACY_FILTER_TAG);
    filters.add(defaultChatFilter);
    return true;
  }

  private boolean isLegacyFilter(ChatFilter filter) {
    return filter.getIncludedTags().getTags().stream()
        .anyMatch(tag -> LEGACY_FILTER_TAG.equals(tag.getContent()));
  }
}
