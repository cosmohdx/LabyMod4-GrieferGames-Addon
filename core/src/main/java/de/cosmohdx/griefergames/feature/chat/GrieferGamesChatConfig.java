package de.cosmohdx.griefergames.feature.chat;

import de.cosmohdx.griefergames.core.config.FeatureConfig;

public class GrieferGamesChatConfig extends FeatureConfig {

  private final ChatCommandsConfig commands = new ChatCommandsConfig();
  private final ChatDisplayConfig display = new ChatDisplayConfig();
  private final SecondChatConfig secondChat = new SecondChatConfig();
  private final MentionConfig mentions = new MentionConfig();
  private final TeleportConfig teleport = new TeleportConfig();
  private final ChatFiltersConfig filters = new ChatFiltersConfig();
  private final MagicPrefixConfig magic = new MagicPrefixConfig();
  private final ChatTimeConfig chatTime = new ChatTimeConfig();

  public ChatCommandsConfig commands() {
    return this.commands;
  }

  public ChatDisplayConfig display() {
    return this.display;
  }

  public SecondChatConfig secondChat() {
    return this.secondChat;
  }

  public MentionConfig mentions() {
    return this.mentions;
  }

  public TeleportConfig teleport() {
    return this.teleport;
  }

  public ChatFiltersConfig filters() {
    return this.filters;
  }

  public MagicPrefixConfig magic() {
    return this.magic;
  }

  public ChatTimeConfig chatTime() {
    return this.chatTime;
  }

  public boolean clickToReply() {
    return this.isEnabled() && this.commands.clickToReply();
  }

  public boolean preventCommandFailure() {
    return this.isEnabled() && this.commands.preventCommandFailure();
  }

  public boolean correctCommandCapitalisation() {
    return this.isEnabled() && this.commands.correctCommandCapitalisation();
  }

  public boolean betterIgnoreList() {
    return this.isEnabled() && this.commands.betterIgnoreList();
  }

  public boolean showPrefixInDisplayName() {
    return this.isEnabled() && this.display.showPrefixInDisplayName();
  }

  public String chatTabName() {
    return this.secondChat.chatTabName();
  }

  public boolean createSecondChat() {
    return this.isEnabled() && this.secondChat.createTab();
  }

  public boolean manageSecondChatFilters() {
    return this.isEnabled() && this.secondChat.manageFilters();
  }

  public boolean useChatIndicators() {
    return this.isEnabled() && this.secondChat.chatIndicators();
  }

  public RealnamePosition realnamePosition() {
    if (!this.isEnabled() || !this.secondChat.isEnabled()) {
      return RealnamePosition.DEFAULT;
    }
    return this.secondChat.realname().position();
  }

  public boolean routePrivateMessages() {
    return this.route(this.secondChat.categories().privateMessages());
  }

  public boolean routePlotChat() {
    return this.route(this.secondChat.categories().plotChat());
  }

  public boolean routeRemover() {
    return this.route(this.secondChat.categories().remover());
  }

  public boolean routePayments() {
    return this.route(this.secondChat.categories().payments());
  }

  public boolean routeBank() {
    return this.route(this.secondChat.categories().bank());
  }

  public Sounds privateMessageSound() {
    if (!this.isEnabled()) {
      return Sounds.NONE;
    }
    return this.secondChat.categories().privateMessages().sound();
  }

  public boolean mentionsEnabled() {
    return this.isEnabled() && this.mentions.isEnabled();
  }

  public boolean highlightTpa() {
    return this.isEnabled() && this.teleport.highlightTpa();
  }

  public boolean hideVoteMessages() {
    return this.isEnabled() && this.filters.hideVoteMessages();
  }

  public boolean hideNewsMessages() {
    return this.isEnabled() && this.filters.hideNewsMessages();
  }

  public boolean hideBlankLines() {
    return this.isEnabled() && this.filters.hideBlankLines();
  }

  public boolean hideSupremeBlankLines() {
    return this.isEnabled() && this.filters.hideSupremeBlankLines();
  }

  public boolean replaceClanTags() {
    return this.isEnabled() && this.magic.replaceClanTags();
  }

  public boolean replaceMagicPrefixes() {
    return this.isEnabled() && this.magic.replacePrefixes();
  }

  public String magicPrefixReplacement() {
    if (!this.replaceMagicPrefixes()) {
      return "";
    }
    return this.magic.replacement();
  }

  public boolean showChatTime() {
    return this.isEnabled() && this.chatTime.isEnabled();
  }

  public boolean chatTimeAfterMessage() {
    return this.showChatTime() && this.chatTime.afterMessage();
  }

  public String chatTimeFormat() {
    String format = this.chatTime.format();
    return format == null ? "" : format;
  }

  private boolean route(SecondChatCategoryConfig category) {
    return this.isEnabled() && this.secondChat.isEnabled() && category.showInSecondChat();
  }
}
