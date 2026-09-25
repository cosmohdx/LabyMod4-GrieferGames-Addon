package de.cosmohdx.griefergames.feature.chat;

import de.cosmohdx.griefergames.GrieferGames;
import de.cosmohdx.griefergames.core.GrieferGamesConfig;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.labymod.api.Laby;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.network.NetworkPlayerInfo;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.network.server.ServerDisconnectEvent;

/**
 * Puts the sender's tab-list head in front of a 1.8 player chat line.
 * Cloud chat and an unknown network are left alone. A missing tab entry means no head.
 */
public class MessageHeads extends ChatModule {

  private static final int ICON_CACHE_LIMIT = 256;

  private final GrieferGames griefergames;
  private final Map<String, Icon> icons = new HashMap<>();

  public MessageHeads(GrieferGames griefergames) {
    this.griefergames = griefergames;
  }

  @Subscribe
  public void messageProcessEvent(GGChatProcessEvent event) {
    if (event.isCancelled() || event.getMessage() == null || event.getMessage().component() == null) {
      return;
    }
    if (!this.griefergames.state().isOnGrieferGames() || !this.griefergames.state().isLegacyNetwork()) {
      return;
    }
    if (!this.griefergames.configuration().chat().showMessageHeads()) {
      return;
    }

    Component component = event.getMessage().component();
    List<Component> children = new ArrayList<>();
    if (component.getChildren() != null) {
      children.addAll(component.getChildren());
    }
    boolean timeBefore = this.griefergames.configuration().chat().showChatTime()
        && !this.griefergames.configuration().chat().chatTimeAfterMessage();
    String format = this.griefergames.configuration().chat().chatTimeFormat();
    if (format.isBlank()) {
      format = GrieferGamesConfig.DEFAULT_CHATTIME_FORMAT;
    }
    boolean stamp = timeBefore
        && !children.isEmpty()
        && ChatTimeMarkers.isLeadingStamp(this.getPlainText(children.get(0)), format);
    String plain = event.getMessage().getPlainText();
    if (stamp) {
      String stampText = this.getPlainText(children.get(0));
      if (plain != null && plain.startsWith(stampText)) {
        String withoutStamp = plain.substring(stampText.length());
        if (ChatSenderExtractor.sender(withoutStamp).isPresent()) {
          plain = withoutStamp;
        }
      }
    }
    Optional<ChatSender> sender = ChatSenderExtractor.sender(plain);
    if (sender.isEmpty() || !this.kindEnabled(sender.get().kind())) {
      return;
    }

    NetworkPlayerInfo player = this.findPlayer(sender.get().name());
    if (player == null || player.profile() == null) {
      return;
    }
    Component marker = Component.empty()
        .append(Component.icon(this.iconFor(player)))
        .append(Component.text(" "));
    children.add(ChatTimeMarkers.insertionIndex(timeBefore, stamp), marker);
    component.setChildren(children);
  }

  @Subscribe
  public void onServerQuit(ServerDisconnectEvent event) {
    this.icons.clear();
  }

  private boolean kindEnabled(ChatSender.Kind kind) {
    return switch (kind) {
      case PUBLIC -> this.griefergames.configuration().chat().messageHeadsGlobal();
      case PRIVATE -> this.griefergames.configuration().chat().messageHeadsPrivate();
      case PLOT -> this.griefergames.configuration().chat().messageHeadsPlot();
    };
  }

  private NetworkPlayerInfo findPlayer(String senderName) {
    if (Laby.labyAPI().minecraft().getClientPacketListener() == null) {
      return null;
    }
    return TabListNames.find(
        Laby.labyAPI().minecraft().getClientPacketListener().getNetworkPlayerInfos(),
        senderName,
        new TabListNames.NameView<>() {
          @Override
          public String profileName(NetworkPlayerInfo entry) {
            if (entry.profile() == null) {
              return "";
            }
            return entry.profile().getUsername();
          }

          @Override
          public String displayPlain(NetworkPlayerInfo entry) {
            if (entry.displayName() == null) {
              return "";
            }
            return MessageHeads.this.getPlainText(entry.displayName());
          }
        });
  }

  private Icon iconFor(NetworkPlayerInfo player) {
    String key = player.profile().getUniqueId() == null
        ? player.profile().getUsername()
        : player.profile().getUniqueId().toString();
    Icon cached = key == null ? null : this.icons.get(key);
    if (cached != null) {
      return cached;
    }
    Icon icon = Icon.head(player.profile());
    if (key != null) {
      if (this.icons.size() >= ICON_CACHE_LIMIT) {
        this.icons.clear();
      }
      this.icons.put(key, icon);
    }
    return icon;
  }
}
