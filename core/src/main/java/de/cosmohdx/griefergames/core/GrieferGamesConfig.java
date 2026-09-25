package de.cosmohdx.griefergames.core;

import de.cosmohdx.griefergames.core.config.GrieferGamesConfigMigration;
import de.cosmohdx.griefergames.feature.afk.GrieferGamesAFKConfig;
import de.cosmohdx.griefergames.feature.automation.GrieferGamesAutomationsConfig;
import de.cosmohdx.griefergames.feature.booster.GrieferGamesBoosterToolsConfig;
import de.cosmohdx.griefergames.feature.chat.GrieferGamesChatConfig;
import de.cosmohdx.griefergames.feature.friends.GrieferGamesFriendsConfig;
import de.cosmohdx.griefergames.feature.itemlist.ItemListActivity;
import de.cosmohdx.griefergames.feature.remover.RemoverConfig;
import de.cosmohdx.griefergames.feature.payment.GrieferGamesPaymentsConfig;
import net.labymod.api.addon.AddonConfig;
import net.labymod.api.client.gui.screen.widget.widgets.input.ButtonWidget.ButtonSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.configuration.loader.annotation.ConfigName;
import net.labymod.api.configuration.loader.annotation.SpriteSlot;
import net.labymod.api.configuration.loader.annotation.SpriteTexture;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.models.OperatingSystem;
import net.labymod.api.util.MethodOrder;

@ConfigName("settings")
@SpriteTexture("settings.png")
public class GrieferGamesConfig extends AddonConfig {

  public static final String DEFAULT_AMP_REPLACEMENT = "[AMP]",
      DEFAULT_AFK_NICKNAME = "AFK_%name%",
      DEFAULT_CHATTIME_FORMAT = "&8[&3{h}&7:&3{m}&7:&3{s}&8]";

  @SpriteSlot(x = 0, y = 0)
  @SwitchSetting
  private final ConfigProperty<Boolean> enabled = new ConfigProperty<>(true);

  @SpriteSlot(x = 2, y = 0)
  private final GrieferGamesChatConfig chat = new GrieferGamesChatConfig();

  @SpriteSlot(x = 3, y = 0)
  private final GrieferGamesPaymentsConfig payment = new GrieferGamesPaymentsConfig();

  @SpriteSlot(x = 2, y = 1)
  private final RemoverConfig remover = new RemoverConfig();

  @SpriteSlot(x = 4, y = 0)
  private final GrieferGamesAutomationsConfig automations = new GrieferGamesAutomationsConfig();

  @SpriteSlot(x = 1, y = 1)
  private final GrieferGamesAFKConfig afk = new GrieferGamesAFKConfig();

  @SpriteSlot(x = 0, y = 1)
  private final GrieferGamesBoosterToolsConfig booster = new GrieferGamesBoosterToolsConfig();

  @SpriteSlot(x = 5, y = 0)
  private final GrieferGamesFriendsConfig friends = new GrieferGamesFriendsConfig();

  @SpriteSlot(x = 7, y = 0)
  @MethodOrder(after = "friends")
  @ButtonSetting
  public void openGithub() {
    OperatingSystem.getPlatform().openUrl("https://github.com/cosmohdx/LabyMod4-GrieferGames-Addon");
  }

  @SpriteSlot(x = 6, y = 0)
  @MethodOrder(after = "openGithub")
  @ButtonSetting
  public void openSupport() {
    OperatingSystem.getPlatform().openUrl("https://discord.gg/EtgdTX9dKa");
  }

  @SpriteSlot(x = 3, y = 1)
  @MethodOrder(after = "openSupport")
  @ButtonSetting
  public void openItemList() {
    ItemListActivity.open();
  }

  @SpriteSlot(x = 4, y = 1)
  @MethodOrder(after = "openItemList")
  @ButtonSetting
  public void openWiki() {
    OperatingSystem.getPlatform().openUrl("https://wiki.griefergames.net/");
  }

  @Override
  public int getConfigVersion() {
    return GrieferGamesConfigMigration.CURRENT_VERSION;
  }

  @Override
  public ConfigProperty<Boolean> enabled() {
    return this.enabled;
  }

  public GrieferGamesChatConfig chat() {
    return this.chat;
  }

  public GrieferGamesPaymentsConfig payment() {
    return this.payment;
  }

  public RemoverConfig remover() {
    return this.remover;
  }

  public GrieferGamesAutomationsConfig automations() {
    return this.automations;
  }

  public GrieferGamesAFKConfig afk() {
    return this.afk;
  }

  public GrieferGamesBoosterToolsConfig booster() {
    return this.booster;
  }

  public GrieferGamesFriendsConfig friends() {
    return this.friends;
  }
}
