package de.cosmohdx.griefergames.feature.kisupport;

import de.cosmohdx.griefergames.GrieferGames;
import de.cosmohdx.griefergames.feature.automation.ChatColor;
import de.cosmohdx.griefergames.feature.chat.ChatModule;
import de.cosmohdx.griefergames.feature.chat.GGChatProcessEvent;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.labymod.api.Laby;
import net.labymod.api.client.chat.ChatMessage;
import net.labymod.api.client.component.Component;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.network.server.ServerDisconnectEvent;
import net.labymod.api.event.client.network.server.ServerJoinEvent;

public class KiSupport extends ChatModule {

  public static final String ALFRED_NAME = "Alfred";

  /**
   * Separator between {@code KI-Support} and {@code Alfred} is often a fancy pipe that some
   * fonts (e.g. the vanilla/dev client) render as a missing-glyph box. Match any non-word
   * separator, not only ASCII {@code |}.
   */
  private static final Pattern ALFRED_MESSAGE = Pattern.compile(
      "(?i).*KI-Support\\W+Alfred\\s*(?:\\u00BB|»).*");
  private static final Pattern MESSAGE_ARROW = Pattern.compile(
      "(?s)(.*(?:\\u00BB|»)\\s*)(.*)");

  private final GrieferGames griefergames;
  private boolean completionApplied;

  public KiSupport(GrieferGames griefergames) {
    this.griefergames = griefergames;
    KiSupportConfig config = griefergames.configuration().kiSupport();
    griefergames.configuration().enabled().addChangeListener(ignored -> this.syncCompletion());
    config.enabled().addChangeListener(ignored -> this.syncCompletion());
    config.alfredCompletionProperty().addChangeListener(ignored -> this.syncCompletion());
  }

  @Subscribe
  public void onServerJoin(ServerJoinEvent event) {
    this.completionApplied = false;
    this.syncCompletion();
    // Connection can still be null on the join tick; retry shortly after.
    this.griefergames.schedule(() -> {
      if (this.griefergames.state().isOnGrieferGames()) {
        this.syncCompletion();
      }
    }, 1, TimeUnit.SECONDS);
  }

  @Subscribe
  public void onServerQuit(ServerDisconnectEvent event) {
    if (this.completionApplied) {
      this.griefergames.controller().removeCustomChatCompletion(ALFRED_NAME);
      this.completionApplied = false;
    }
  }

  @Subscribe
  public void messageProcessEvent(GGChatProcessEvent event) {
    if (event.isCancelled()) {
      return;
    }
    if (!this.griefergames.configuration().enabled().get()
        || !this.griefergames.state().isOnGrieferGames()) {
      return;
    }
    KiSupportConfig config = this.griefergames.configuration().kiSupport();
    if (!config.isEnabled()) {
      return;
    }

    String plain = event.getMessage().getPlainText();
    if (plain == null || plain.isBlank() || !ALFRED_MESSAGE.matcher(plain).matches()) {
      return;
    }

    if (config.hideAlfred()) {
      event.setCancelled(true);
      return;
    }

    if (config.alfredSubtle()) {
      this.makeSubtle(event.getMessage(), config.alfredColor());
    }
  }

  public void syncCompletion() {
    boolean shouldAdd = this.griefergames.configuration().enabled().get()
        && this.griefergames.state().isOnGrieferGames()
        && this.griefergames.configuration().kiSupport().alfredCompletion();

    if (shouldAdd) {
      this.griefergames.controller().addCustomChatCompletion(ALFRED_NAME);
      this.completionApplied = true;
    } else if (this.completionApplied) {
      this.griefergames.controller().removeCustomChatCompletion(ALFRED_NAME);
      this.completionApplied = false;
    }
  }

  private void makeSubtle(ChatMessage message, ChatColor color) {
    String formatted = this.getFormattedText(message.component());
    if (formatted == null || formatted.isBlank()) {
      return;
    }

    formatted = formatted.replaceAll("(?i)§l", "");

    String colorCode = color == null ? "" : color.getColorCode();
    if (colorCode != null && !colorCode.isBlank()) {
      Matcher matcher = MESSAGE_ARROW.matcher(formatted);
      if (matcher.matches()) {
        String body = matcher.group(2).replaceAll("(?i)§[0-9a-f]", "");
        formatted = matcher.group(1) + "§" + colorCode + body;
      }
    }

    Component edited = Laby.references().componentRenderer()
        .legacySectionSerializer()
        .deserialize(formatted);
    if (edited != null) {
      message.edit(edited);
    }
  }
}
