package de.neocraftr.griefergames.settings;

import de.neocraftr.griefergames.enums.RealnamePosition;
import de.neocraftr.griefergames.enums.Sounds;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.TextFieldWidget.TextFieldSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.dropdown.DropdownWidget.DropdownSetting;
import net.labymod.api.configuration.loader.Config;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.configuration.settings.annotation.SettingSection;

public class GrieferGamesChatConfig extends Config {

  //@ParentSwitch
  @SwitchSetting
  private final ConfigProperty<Boolean> enabled = new ConfigProperty<Boolean>(true);

  // ONLY 1.8
  @SwitchSetting
  private final ConfigProperty<Boolean> clickToReply = new ConfigProperty<>(true);

  @SwitchSetting
  private final ConfigProperty<Boolean> preventCommandFailure = new ConfigProperty<>(true);

  @SwitchSetting
  private final ConfigProperty<Boolean> correctCommandCapitalisation = new ConfigProperty<>(true);

  // Only 1.8
  @SwitchSetting
  private final ConfigProperty<Boolean> betterIgnoreList = new ConfigProperty<>(true);

  @SwitchSetting
  private final ConfigProperty<Boolean> showPrefixInDisplayName = new ConfigProperty<>(true);

  @SettingSection("highlightMessages")

  private GrieferGamesChatTabConfig tabConfig = new GrieferGamesChatTabConfig();

  @SwitchSetting
  private final ConfigProperty<Boolean> plotChatRight = new ConfigProperty<>(true);

  @SwitchSetting
  private final ConfigProperty<Boolean> privateChatRight = new ConfigProperty<>(true);

  @DropdownSetting
  private final ConfigProperty<Sounds> privateChatSound = new ConfigProperty<>(Sounds.POP);

  // 1.8
  @DropdownSetting
  private final ConfigProperty<RealnamePosition> realnamePosition = new ConfigProperty<>(
      RealnamePosition.DEFAULT);

  private final GrieferGamesNameHighlightConfig nameHighlightConfig = new GrieferGamesNameHighlightConfig();

  @SwitchSetting
  private final ConfigProperty<Boolean> highlightTPA = new ConfigProperty<>(true);

  @SwitchSetting
  private final ConfigProperty<Boolean> itemRemoverChatRight = new ConfigProperty<>(false);

  @SwitchSetting
  private final ConfigProperty<Boolean> itemRemoverLastTimeHover = new ConfigProperty<>(true);

  @SwitchSetting
  private final ConfigProperty<Boolean> itemRemoverNotification = new ConfigProperty<>(true);

  @SwitchSetting
  private final ConfigProperty<Boolean> mobRemoverChatRight = new ConfigProperty<>(false);

  @SwitchSetting
  private final ConfigProperty<Boolean> mobRemoverLastTimeHover = new ConfigProperty<>(true);

  @SwitchSetting
  private final ConfigProperty<Boolean> mobRemoverNotification = new ConfigProperty<>(true);

  @SettingSection("blockMessages")

  @SwitchSetting
  private final ConfigProperty<Boolean> hideVoteMessages = new ConfigProperty<>(false);

  @SwitchSetting
  private final ConfigProperty<Boolean> hideNewsMessages = new ConfigProperty<>(false);

  @SwitchSetting
  private final ConfigProperty<Boolean> hideBlankLines = new ConfigProperty<>(true);

  @SwitchSetting
  private final ConfigProperty<Boolean> hideSupremeBlankLines = new ConfigProperty<>(true);

  @SettingSection("magicPrefix")

  @SwitchSetting
  private final ConfigProperty<Boolean> ampClantagEnabled = new ConfigProperty<>(true);

  @SwitchSetting
  private final ConfigProperty<Boolean> ampEnabled = new ConfigProperty<>(true);

  @TextFieldSetting
  private final ConfigProperty<String> ampReplacement = new ConfigProperty<>(GrieferGamesConfig.DEFAULT_AMP_REPLACEMENT);

  @SettingSection("chatTime")

  @SwitchSetting
  private final ConfigProperty<Boolean> showChatTime = new ConfigProperty<>(false);

  @SwitchSetting
  private final ConfigProperty<Boolean> chatTimeAfterMessage = new ConfigProperty<>(false);

  @TextFieldSetting
  private final ConfigProperty<String> chatTimeFormat = new ConfigProperty<>(GrieferGamesConfig.DEFAULT_CHATTIME_FORMAT);

  public boolean isEnabled() {
    return enabled.get();
  }

  public boolean isPreventCommandFailure() {
    return enabled.get() && preventCommandFailure.get();
  }

  public ConfigProperty<Boolean> correctCommandCapitalisation() {
    return correctCommandCapitalisation;
  }

  public boolean isBetterIgnoreList() {
    return enabled.get() && betterIgnoreList.get();
  }

  public boolean isShowPrefixInDisplayName() {
    return enabled.get() && showPrefixInDisplayName.get();
  }

  public GrieferGamesChatTabConfig tabConfig() {
    return tabConfig;
  }

  public String getChatTabName() {
    return tabConfig.getChatTabName();
  }

  public boolean isPlotChatRight() {
    return enabled.get() && plotChatRight.get();
  }

  public boolean isClickToReply() {
    return enabled.get() && clickToReply.get();
  }

  public boolean isPrivateChatRight() {
    return enabled.get() && privateChatRight.get();
  }

  public Sounds getPrivateChatSound() {
    if(!enabled.get()) return Sounds.NONE;
    return privateChatSound.get();
  }

  public RealnamePosition getRealnamePosition() {
    if(!enabled.get()) return RealnamePosition.DEFAULT;
    return realnamePosition.get();
  }

  public GrieferGamesNameHighlightConfig nameHighlightConfig() {
    return nameHighlightConfig;
  }

  public boolean isHighlightTpa() {
    return enabled.get() && highlightTPA.get();
  }

  public boolean isRemoverChatRight() {
    return enabled.get() && itemRemoverChatRight.get();
  }

  public boolean isRemoverLastTimeHover() {
    return enabled.get() && itemRemoverLastTimeHover.get();
  }

  public boolean isRemoverNotification() {
    return enabled.get() && itemRemoverNotification.get();
  }

  public boolean isMobRemoverChatRight() {
    return enabled.get() && mobRemoverChatRight.get();
  }

  public boolean isMobRemoverLastTimeHover() {
    return enabled.get() && mobRemoverLastTimeHover.get();
  }

  public boolean isMobRemoverNotification() {
    return enabled.get() && mobRemoverNotification.get();
  }

  public boolean isHideVoteMessages() {
    return enabled.get() && hideVoteMessages.get();
  }

  public boolean isHideNewsMessages() {
    return enabled.get() && hideNewsMessages.get();
  }

  public boolean isHideBlankLines() {
    return enabled.get() && hideBlankLines.get();
  }

  public boolean isHideSupremeBlankLines() {
    return enabled.get() && hideSupremeBlankLines.get();
  }

  public boolean isAmpClantagEnabled() {
    return enabled.get() && ampClantagEnabled.get();
  }

  public boolean isAmpEnabled() {
    return enabled.get() && ampEnabled.get();
  }

  public String getAmpReplacement() {
    if(!enabled.get()) return "";
    return ampReplacement.get();
  }

  public boolean isShowChatTime() {
    return enabled.get() && showChatTime.get();
  }

  public ConfigProperty<Boolean> chatTimeAfterMessage() {
    return chatTimeAfterMessage;
  }

  public ConfigProperty<String> chatTimeFormat() {
    return chatTimeFormat;
  }
}
