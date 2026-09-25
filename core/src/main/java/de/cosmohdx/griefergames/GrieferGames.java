package de.cosmohdx.griefergames;

import de.cosmohdx.griefergames.core.AddonState;
import de.cosmohdx.griefergames.core.config.ConfigMigrationListener;
import de.cosmohdx.griefergames.core.GGMessageCommand;
import de.cosmohdx.griefergames.core.GrieferGamesConfig;
import de.cosmohdx.griefergames.core.GrieferGamesController;
import de.cosmohdx.griefergames.core.Helper;
import de.cosmohdx.griefergames.core.generated.DefaultReferenceStorage;
import de.cosmohdx.griefergames.feature.afk.AfkListener;
import de.cosmohdx.griefergames.feature.automation.AutoPortalListener;
import de.cosmohdx.griefergames.feature.booster.BoosterChatModule;
import de.cosmohdx.griefergames.feature.booster.BoosterController;
import de.cosmohdx.griefergames.feature.booster.BoosterHudWidget;
import de.cosmohdx.griefergames.feature.booster.BoosterListener;
import de.cosmohdx.griefergames.feature.chat.AntiMagicClanTag;
import de.cosmohdx.griefergames.feature.chat.AntiMagicPrefix;
import de.cosmohdx.griefergames.feature.chat.BetterIgnoreList;
import de.cosmohdx.griefergames.feature.chat.Blanks;
import de.cosmohdx.griefergames.feature.chat.ChatTime;
import de.cosmohdx.griefergames.feature.chat.GGKeyListener;
import de.cosmohdx.griefergames.feature.chat.GGMessageReceiveListener;
import de.cosmohdx.griefergames.feature.chat.GGMessageSendListener;
import de.cosmohdx.griefergames.feature.chat.GGNameTagListener;
import de.cosmohdx.griefergames.feature.chat.Mention;
import de.cosmohdx.griefergames.feature.chat.News;
import de.cosmohdx.griefergames.feature.chat.PlotChat;
import de.cosmohdx.griefergames.feature.chat.PrivateMessage;
import de.cosmohdx.griefergames.feature.chat.Realname;
import de.cosmohdx.griefergames.feature.chat.Teleport;
import de.cosmohdx.griefergames.feature.chat.Vote;
import de.cosmohdx.griefergames.feature.delay.DelayHudWidget;
import de.cosmohdx.griefergames.feature.delay.DelaySubServerListener;
import de.cosmohdx.griefergames.feature.delay.WaitTime;
import de.cosmohdx.griefergames.feature.fly.FlyHudWidget;
import de.cosmohdx.griefergames.feature.friends.FriendsPresenceListener;
import de.cosmohdx.griefergames.feature.remover.Remover;
import de.cosmohdx.griefergames.feature.nickname.Nickname;
import de.cosmohdx.griefergames.feature.nickname.NicknameHudWidget;
import de.cosmohdx.griefergames.feature.payment.Bank;
import de.cosmohdx.griefergames.feature.payment.FileManager;
import de.cosmohdx.griefergames.feature.payment.IncomeHudWidget;
import de.cosmohdx.griefergames.feature.payment.Payment;
import de.cosmohdx.griefergames.feature.redstone.RedstoneHudWidget;
import de.cosmohdx.griefergames.feature.redstone.RedstoneListener;
import de.cosmohdx.griefergames.feature.server.GGServerJoinListener;
import de.cosmohdx.griefergames.feature.server.GGServerQuitListener;
import de.cosmohdx.griefergames.feature.subserver.GGScoreboardListener;
import de.cosmohdx.griefergames.feature.subtitle.UserSubtitleListener;
import de.cosmohdx.griefergames.payload.PayloadReceiver;
import de.cosmohdx.griefergames.feature.subserver.GGSubServerChangeListener;
import de.cosmohdx.griefergames.feature.subserver.SubServerHUDWidget;
import net.labymod.api.addon.LabyAddon;
import net.labymod.api.client.chat.ChatMessage;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.NamedTextColor;
import net.labymod.api.client.gui.hud.binding.category.HudWidgetCategory;
import net.labymod.api.client.options.ChatVisibility;
import net.labymod.api.configuration.labymod.chat.AdvancedChatMessage;
import net.labymod.api.models.addon.annotation.AddonMain;
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
  private final AddonState state = new AddonState();
  private Helper helper;
  private GrieferGamesController controller;
  private FileManager fileManager;
  private BoosterController boosterController;
  private PayloadReceiver payloadReceiver;
  private HudWidgetCategory hudWidgetCategory;
  private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(runnable -> {
    Thread thread = new Thread(runnable, "griefergames-addon");
    thread.setDaemon(true);
    return thread;
  });

  @Override
  protected void preConfigurationLoad() {
    registerListener(new ConfigMigrationListener());
  }

  @Override
  protected void enable() {
    DefaultReferenceStorage reference = referenceStorageAccessor();
    griefergames = this;
    fileManager = new FileManager(this);
    helper = new Helper(this);
    controller = reference.getGrieferGamesController();
    boosterController = new BoosterController(this);
    payloadReceiver = new PayloadReceiver(this);

    registerSettingCategory();
    registerListener(payloadReceiver);
    registerListener(new RedstoneListener(this));
    new UserSubtitleListener(this);
    registerListener(new GGServerJoinListener(this));
    registerListener(new GGServerQuitListener(this));
    registerListener(new GGMessageSendListener(this));
    registerListener(new GGMessageReceiveListener(this));
    registerListener(new GGKeyListener(this));
    registerListener(new GGNameTagListener(this));
    registerListener(new GGScoreboardListener(this));
    registerListener(new BoosterListener(this));
    registerListener(new FriendsPresenceListener(this));
    registerListener(new DelaySubServerListener(this));
    registerListener(new GGSubServerChangeListener(this));
    registerListener(new AutoPortalListener(this));
    registerListener(new AfkListener(this));

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
    registerListener(new Remover(this));
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

  @Override
  protected void onDeactivated() {
    if (fileManager != null) {
      fileManager.close();
    }
  }

  @Override
  protected void onActivated() {
    if (fileManager != null) {
      fileManager.open();
    }
  }

  public void schedule(Runnable runnable, long delay, TimeUnit unit) {
    scheduler.schedule(runnable, delay, unit);
  }

  public void sendToSecondChat(String msg) {
    if (state.getSecondChat() == null) {
      return;
    }
    AdvancedChatMessage chatMessage = AdvancedChatMessage.chat(ChatMessage.builder()
        .component(Component.text(msg))
        .visibility(ChatVisibility.SHOWN)
        .build());
    state.getSecondChat().handleInput(chatMessage);
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

  public PayloadReceiver payloads() {
    return payloadReceiver;
  }

  public AddonState state() {
    return state;
  }

  public String namespace() {
    return this.addonInfo().getNamespace();
  }

  public HudWidgetCategory getHudWidgetCategory() {
    return hudWidgetCategory;
  }
}
