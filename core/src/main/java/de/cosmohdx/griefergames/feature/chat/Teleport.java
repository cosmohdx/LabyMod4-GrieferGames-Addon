package de.cosmohdx.griefergames.feature.chat;

import de.cosmohdx.griefergames.GrieferGames;
import de.cosmohdx.griefergames.feature.chat.GGChatProcessEvent;
import de.cosmohdx.griefergames.core.SubServerType;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.NamedTextColor;
import net.labymod.api.client.component.format.Style;
import net.labymod.api.client.component.format.TextDecoration;
import net.labymod.api.event.Subscribe;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Teleport extends ChatModule {
  private final GrieferGames griefergames;
  private final Pattern tpaMesssageRegexp = Pattern.compile("^([A-Za-z\\-\\+]+) \\u2503 (~?\\!?\\w{1,16}) möchte sich zu dir teleportieren\\.$");
  private final Pattern tpahereMesssageRegexp = Pattern.compile("^([A-Za-z\\-\\+]+) \\u2503 (~?\\!?\\w{1,16}) möchte, dass du dich zu der Person teleportierst\\.$");

  public Teleport(GrieferGames griefergames) {
    this.griefergames = griefergames;
  }

  @Subscribe
  public void messageProcessEvent(GGChatProcessEvent event) {
    if(event.isCancelled()) return;
    if(!griefergames.state().isSubServerType(SubServerType.REGULAR)) return;
    if (!griefergames.configuration().chatConfig().isHighlightTpa()) return;
    if (event.getMessage().getPlainText().isBlank()) return;

    Matcher tpaMesssage = tpaMesssageRegexp.matcher(event.getMessage().getPlainText());
    if (tpaMesssage.find()) {
      List<Component> children = new ArrayList<>(event.getMessage().component().getChildren());
      children.add(0, Component.text("[TPA] ", Style.builder()
        .color(NamedTextColor.DARK_GREEN)
        .decorate(TextDecoration.BOLD)
        .build()));
      event.getMessage().component().setChildren(children);
    }

    Matcher tpahereMesssage = tpahereMesssageRegexp.matcher(event.getMessage().getPlainText());
    if (tpahereMesssage.find()) {
      List<Component> children = new ArrayList<>(event.getMessage().component().getChildren());
      children.add(0, Component.text("[TPAHERE] ", Style.builder()
        .color(NamedTextColor.RED)
        .decorate(TextDecoration.BOLD)
        .build()));
      event.getMessage().component().setChildren(children);
    }
  }
}
