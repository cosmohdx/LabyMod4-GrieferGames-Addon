package de.cosmohdx.griefergames;

import de.cosmohdx.griefergames.core.GGMessageCommand;
import de.cosmohdx.griefergames.core.GrieferGamesConfig;
import de.cosmohdx.griefergames.core.GrieferGamesController;
import de.cosmohdx.griefergames.core.Helper;
import de.cosmohdx.griefergames.core.SubServerType;
import de.cosmohdx.griefergames.core.generated.DefaultReferenceStorage;
import de.cosmohdx.griefergames.feature.automation.DelayHudWidget;
import de.cosmohdx.griefergames.feature.automation.GGTickListener;
import de.cosmohdx.griefergames.feature.automation.WaitTime;
import de.cosmohdx.griefergames.feature.booster.BoosterChatModule;
import de.cosmohdx.griefergames.feature.booster.BoosterController;
import de.cosmohdx.griefergames.feature.booster.BoosterHudWidget;
import de.cosmohdx.griefergames.feature.chat.AntiMagicClanTag;
import de.cosmohdx.griefergames.feature.chat.AntiMagicPrefix;
import de.cosmohdx.griefergames.feature.chat.BetterIgnoreList;
import de.cosmohdx.griefergames.feature.chat.Blanks;
import de.cosmohdx.griefergames.feature.chat.ChatTime;
import de.cosmohdx.griefergames.feature.chat.GGKeyListener;
import de.cosmohdx.griefergames.feature.chat.GGMessageReceiveListener;
import de.cosmohdx.griefergames.feature.chat.GGMessageSendListener;
import de.cosmohdx.griefergames.feature.chat.GGNameTagListener;
import de.cosmohdx.griefergames.feature.chat.ItemRemover;
import de.cosmohdx.griefergames.feature.chat.Mention;
import de.cosmohdx.griefergames.feature.chat.MobRemover;
import de.cosmohdx.griefergames.feature.chat.News;
import de.cosmohdx.griefergames.feature.chat.Nickname;
import de.cosmohdx.griefergames.feature.chat.NicknameHudWidget;
import de.cosmohdx.griefergames.feature.chat.PlotChat;
import de.cosmohdx.griefergames.feature.chat.PrivateMessage;
import de.cosmohdx.griefergames.feature.chat.Realname;
import de.cosmohdx.griefergames.feature.chat.Teleport;
import de.cosmohdx.griefergames.feature.chat.Vote;
import de.cosmohdx.griefergames.feature.payment.Bank;
import de.cosmohdx.griefergames.feature.payment.FileManager;
import de.cosmohdx.griefergames.feature.payment.IncomeHudWidget;
import de.cosmohdx.griefergames.feature.payment.Payment;
import de.cosmohdx.griefergames.feature.server.FlyHudWidget;
import de.cosmohdx.griefergames.feature.server.GGScoreboardListener;
import de.cosmohdx.griefergames.feature.server.GGServerJoinListener;
import de.cosmohdx.griefergames.feature.server.GGServerMessageListener;
import de.cosmohdx.griefergames.feature.server.GGServerQuitListener;
import de.cosmohdx.griefergames.feature.server.GGSubServerChangeListener;
import de.cosmohdx.griefergames.feature.server.RedstoneHudWidget;
import de.cosmohdx.griefergames.feature.server.SubServerHUDWidget;
import net.labymod.api.addon.LabyAddon;
import net.labymod.api.client.chat.ChatMessage;
import net.labymod.api.client.chat.advanced.IngameChatTab;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.NamedTextColor;
import net.labymod.api.client.gui.hud.binding.category.HudWidgetCategory;
import net.labymod.api.client.options.ChatVisibility;
import net.labymod.api.configuration.labymod.chat.AdvancedChatMessage;
import net.labymod.api.models.addon.annotation.AddonMain;
import org.jetbrains.annotations.Nullable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@AddonMain
public class GrieferGames extends LabyAddon<GrieferGamesConfig> {

  public static final Component PREFIX = Component.empty()
      .append(Component.text("[", NamedTextColor.DARK_GRAY))
      .append(Component.text("GrieferGames-Addon", NamedTextColor.GOLD))
      .append(Component.text("] ", NamedTextColor.DARK_GRAY));
  public static final String LOG_PREFIX = "[GrieferGames-Addon] ";

  private static GrieferGames griefergames;
  private Helper helper;
  private GrieferGamesController controller;
  private FileManager fileManager;
  private BoosterController boosterController;

  private boolean onGrieferGames = false;
  private IngameChatTab secondChat = null;
  private HudWidgetCategory hudWidgetCategory = null;
  private String nickname = null;
  private double income = 0;
  private boolean redstoneActive = false;
  private long waitTime = 0;
  private boolean citybuildDelay = false;
  private String subServer = "";
  private SubServerType subServerType = SubServerType.REGULAR;
  private long lastActivity = 0;
  private boolean afk = false;
  private boolean hideBoosterMenu = false;
  private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(runnable -> {
    Thread thread = new Thread(runnable, "griefergames-addon");
    thread.setDaemon(true);
    return thread;
  });

  @Override
  protected void enable() {
    DefaultReferenceStorage reference = referenceStorageAccessor();
    griefergames = this;
    fileManager = new FileManager(this);
    helper = new Helper(this);
    controller = reference.getGrieferGamesController();
    boosterController = new BoosterController(this);

    registerSettingCategory();
    registerListener(new GGServerJoinListener(this));
    registerListener(new GGServerQuitListener(this));
    registerListener(new GGMessageSendListener(this));
    registerListener(new GGMessageReceiveListener(this));
    registerListener(new GGKeyListener(this));
    registerListener(new GGServerMessageListener(this));
    registerListener(new GGScoreboardListener(this));
    registerListener(new GGSubServerChangeListener(this));
    registerListener(new GGTickListener(this));
    registerListener(new GGNameTagListener(this));

    // Chat modules
    registerListener(new Blanks(this));
    registerListener(new PrivateMessage(this));
    registerListener(new Payment(this));
    registerListener(new Bank(this));
    registerListener(new AntiMagicClanTag(this));
    registerListener(new AntiMagicPrefix(this));
    registerListener(new News(this));
    registerListener(new PlotChat(this));
    registerListener(new Vote(this));
    registerListener(new Realname(this));
    registerListener(new ItemRemover(this));
    registerListener(new MobRemover(this));
    registerListener(new BetterIgnoreList(this));
    registerListener(new Mention(this));
    registerListener(new Nickname(this));
    registerListener(new Teleport(this));
    registerListener(new BoosterChatModule(this));
    registerListener(new ChatTime(this));
    registerListener(new WaitTime(this));

    // Hud widgets
    hudWidgetCategory = new HudWidgetCategory(this, namespace());
    labyAPI().hudWidgetRegistry().categoryRegistry().register(hudWidgetCategory);
    labyAPI().hudWidgetRegistry().register(new IncomeHudWidget(this));
    labyAPI().hudWidgetRegistry().register(new NicknameHudWidget(this));
    labyAPI().hudWidgetRegistry().register(new RedstoneHudWidget(this));
    labyAPI().hudWidgetRegistry().register(new DelayHudWidget(this));
    labyAPI().hudWidgetRegistry().register(new FlyHudWidget(this));
    labyAPI().hudWidgetRegistry().register(new BoosterHudWidget(this));
    labyAPI().hudWidgetRegistry().register(new SubServerHUDWidget(this));

    if(labyAPI().labyModLoader().isAddonDevelopmentEnvironment()) {
      registerCommand(new GGMessageCommand(this));
    }

    logger().info(LOG_PREFIX+"Addon successfully enabled.");
  }

  public void schedule(Runnable runnable, long delay, TimeUnit unit) {
    scheduler.schedule(runnable, delay, unit);
  }

  public void sendToSecondChat(String msg) {
    AdvancedChatMessage chatMessage = AdvancedChatMessage.chat(ChatMessage.builder()
        .component(Component.text(msg))
        .visibility(ChatVisibility.SHOWN)
        .build());
    secondChat.handleInput(chatMessage);
  }

  public void displayAddonMessage(String message) {
    displayAddonMessage(Component.text(message));
  }
  public void displayAddonMessage(Component message) {
    displayMessage(Component.empty().append(PREFIX).append(message));
  }

  @Override
  protected Class configurationClass() {
    return GrieferGamesConfig.class;
  }

  public static GrieferGames get() {
    return griefergames;
  }

  public FileManager fileManager() {
    return fileManager;
  }

  public Helper helper() {
    return helper;
  }

  public GrieferGamesController controller() {
    return controller;
  }

  public BoosterController boosterController() {
    return boosterController;
  }


  public String namespace() {
    return this.addonInfo().getNamespace();
  }

  public boolean isOnGrieferGames() {
    return onGrieferGames;
  }
  public void setOnGrieferGames(boolean onGrieferGames) {
    this.onGrieferGames = onGrieferGames;
  }

  @Nullable
  public IngameChatTab getSecondChat() {
    return secondChat;
  }
  public void setSecondChat(@Nullable IngameChatTab secondChat) {
    this.secondChat = secondChat;
  }

  public HudWidgetCategory getHudWidgetCategory() {
    return hudWidgetCategory;
  }

  public String getNickname() {
    return nickname;
  }
  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  public double getIncome() {
    return income;
  }
  public void setIncome(double income) {
    this.income = income;
  }
  public void addIncome(double income) {
    this.income += income;
  }

  public boolean isRedstoneActive() {
    return redstoneActive;
  }
  public void setRedstoneActive(boolean redstoneActive) {
    this.redstoneActive = redstoneActive;
  }

  public long getWaitTime() {
    return waitTime;
  }
  public void setWaitTime(long waitTime) {
    this.waitTime = waitTime;
  }

  public boolean isCitybuildDelay() {
    return citybuildDelay;
  }
  public void setCitybuildDelay(boolean citybuildDelay) {
    this.citybuildDelay = citybuildDelay;
  }

  public String getSubServer() {
    return subServer;
  }
  public void setSubServer(String subServer) {
    this.subServer = subServer;
  }

  public SubServerType getSubServerType() {
    return subServerType;
  }

  public void setSubServerType(SubServerType subServerType) {
    this.subServerType = subServerType;
  }

  public boolean isSubServerType(SubServerType subServerType) {
    return this.subServerType == subServerType;
  }

  public long getLastActivity() {
    return lastActivity;
  }
  public void setLastActivity(long lastActivity) {
    this.lastActivity = lastActivity;
  }

  public boolean isAfk() {
    return afk;
  }
  public void setAfk(boolean afk) {
    this.afk = afk;
  }

  public boolean isHideBoosterMenu() {
    return hideBoosterMenu;
  }
  public void setHideBoosterMenu(boolean hideBoosterMenu) {
    this.hideBoosterMenu = hideBoosterMenu;
  }
}
