package de.cosmohdx.griefergames.feature.chat;

import de.cosmohdx.griefergames.GrieferGames;
import de.cosmohdx.griefergames.feature.chat.GGChatProcessEvent;
import de.cosmohdx.griefergames.feature.chat.Sounds;
import de.cosmohdx.griefergames.core.SubServerType;
import de.cosmohdx.griefergames.feature.chat.GrieferGamesNameHighlightConfig;
import net.labymod.api.Laby;
import net.labymod.api.client.chat.ChatMessage;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.options.ChatVisibility;
import net.labymod.api.client.resources.ResourceLocation;
import net.labymod.api.event.Subscribe;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static de.cosmohdx.griefergames.Constants.CHAT_METADATA_CUSTOM_BACKGROUND;

public class Mention extends ChatModule {
  private final GrieferGames griefergames;
  private final Pattern globalChatregex = Pattern.compile("([A-Za-z\\-\\+]+) \\u2503 (~?\\!?\\w{1,16}) [\\u00BB:] (.+)");

  public Mention(GrieferGames griefergames) {
    this.griefergames = griefergames;
  }

  @Subscribe
  public void messageProcessEvent(GGChatProcessEvent event) {
    if(event.isCancelled()) return;
    GrieferGamesNameHighlightConfig highlightConfig = griefergames.configuration().chatConfig().nameHighlightConfig();
    if(!highlightConfig.isEnabled()) {
      return;
    }
    if (event.getMessage().getPlainText().isBlank()) return;

    Matcher matcher = globalChatregex.matcher(griefergames.helper().removeLeadingMiscCodes(event.getMessage().getPlainText()));
    if(!matcher.find()) {
      return;
    }
    if(matcher.group(2).equalsIgnoreCase(Laby.labyAPI().getName())) {
      // Own message
      return;
    }
    String messageString = matcher.group(3).toLowerCase();
    if(!messageString.contains(Laby.labyAPI().getName().toLowerCase()) &&
        (highlightConfig.getAdditionalHighlightText().isBlank() || !messageString.contains(highlightConfig.getAdditionalHighlightText().toLowerCase()))) {
      // Does not contain the name or the additional text
      return;
    }

    event.getMessage().metadata().set(CHAT_METADATA_CUSTOM_BACKGROUND, highlightConfig.getMentionColor());

    if (highlightConfig.getMentionSound() != Sounds.NONE) {
      ResourceLocation resource = ResourceLocation.create("minecraft", highlightConfig.getMentionSound().path());
      Laby.labyAPI().minecraft().sounds().playSound(resource, 1f, 1f);
    }
  }
}
