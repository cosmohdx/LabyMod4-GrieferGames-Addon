package de.cosmohdx.griefergames.core;

import de.cosmohdx.griefergames.core.config.GrieferGamesConfigMigration;
import de.cosmohdx.griefergames.feature.afk.GrieferGamesAFKConfig;
import de.cosmohdx.griefergames.feature.automation.GrieferGamesAutomationsConfig;
import de.cosmohdx.griefergames.feature.booster.GrieferGamesBoosterToolsConfig;
import de.cosmohdx.griefergames.feature.chat.GrieferGamesChatConfig;
import de.cosmohdx.griefergames.feature.friends.GrieferGamesFriendsConfig;
import de.cosmohdx.griefergames.feature.itemlist.ItemListConfig;
import de.cosmohdx.griefergames.feature.nearby.NearbyPlayersConfig;
import de.cosmohdx.griefergames.feature.remover.RemoverConfig;
import de.cosmohdx.griefergames.feature.payment.GrieferGamesPaymentsConfig;
import de.cosmohdx.griefergames.feature.plotborder.PlotBorderConfig;
import net.labymod.api.addon.AddonConfig;
import de.cosmohdx.griefergames.feature.wiki.WikiActivity;
import net.labymod.api.Laby;
import net.labymod.api.client.gui.screen.key.Key;
import net.labymod.api.client.gui.screen.widget.widgets.input.KeybindWidget.KeyBindSetting;
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

  @SpriteSlot(x = 3, y = 1)
  private final PlotBorderConfig plotBorders = new PlotBorderConfig();

  @SpriteSlot(x = 4, y = 1)
  private final NearbyPlayersConfig nearbyPlayers = new NearbyPlayersConfig();

  @SpriteSlot(x = 5, y = 1)
  private final ItemListConfig itemList = new ItemListConfig();

  @SwitchSetting
  private final ConfigProperty<Boolean> mapTooltipPreview = new ConfigProperty<>(true);

  @SwitchSetting
  private final ConfigProperty<Boolean> headTooltipPreview = new ConfigProperty<>(true);

  @SwitchSetting
  private final ConfigProperty<Boolean> headEnchantmentGlint = new ConfigProperty<>(true);

  @SwitchSetting
  private final ConfigProperty<Boolean> overstackingFix = new ConfigProperty<>(true);

  @KeyBindSetting
  private final ConfigProperty<Key> wikiKey = new ConfigProperty<>(Key.F9);

  // Link buttons stay below every feature config. New configs go above this
  // block, and openGithub keeps after = the last feature field.
  @SpriteSlot(x = 7, y = 0)
  @MethodOrder(after = "itemList")
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

  @SpriteSlot(x = 6, y = 1)
  @MethodOrder(after = "openSupport")
  @ButtonSetting
  public void openWiki() {
    Laby.labyAPI().minecraft().minecraftWindow().displayScreen(new WikiActivity());
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

  public ConfigProperty<Boolean> mapTooltipPreview() { return this.mapTooltipPreview; }
  public ConfigProperty<Boolean> headTooltipPreview() { return this.headTooltipPreview; }
  public ConfigProperty<Boolean> headEnchantmentGlint() { return this.headEnchantmentGlint; }
  public ConfigProperty<Boolean> overstackingFix() { return this.overstackingFix; }

  public ConfigProperty<Key> wikiKey() {
    return this.wikiKey;
  }

  public GrieferGamesFriendsConfig friends() {
    return this.friends;
  }

  public PlotBorderConfig plotBorders() {
    return this.plotBorders;
  }

  public NearbyPlayersConfig nearbyPlayers() {
    return this.nearbyPlayers;
  }

  public ItemListConfig itemList() {
    return this.itemList;
  }
}
