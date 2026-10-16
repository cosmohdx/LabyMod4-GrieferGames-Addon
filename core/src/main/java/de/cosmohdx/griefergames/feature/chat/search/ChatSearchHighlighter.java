package de.cosmohdx.griefergames.feature.chat.search;

import java.util.ArrayList;
import java.util.List;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.TextComponent;
import net.labymod.api.client.component.format.NamedTextColor;
import net.labymod.api.client.component.format.TextDecoration;

/**
 * Marks query hits inside a copied chat component. Click and hover events on the original
 * style stay attached to every slice.
 */
public final class ChatSearchHighlighter {

  private ChatSearchHighlighter() {
  }

  public static Component highlight(Component source, ChatSearchQuery query) {
    if (source == null) {
      return Component.empty();
    }
    Component copy = source.copy();
    if (query == null || query.isEmpty() || query.isInvalid()) {
      return copy;
    }
    if (copy instanceof TextComponent text) {
      List<Component> parts = split(text, query);
      if (parts.size() == 1) {
        return parts.get(0);
      }
      Component root = Component.empty();
      for (Component part : parts) {
        root.append(part);
      }
      return root;
    }
    rewrite(copy, query);
    return copy;
  }

  private static void rewrite(Component component, ChatSearchQuery query) {
    List<Component> next = new ArrayList<>();
    for (Component child : new ArrayList<>(component.getChildren())) {
      if (child instanceof TextComponent text) {
        next.addAll(split(text, query));
      } else {
        rewrite(child, query);
        next.add(child);
      }
    }
    component.setChildren(next);
  }

  private static List<Component> split(TextComponent text, ChatSearchQuery query) {
    String value = text.getText() == null ? "" : text.getText();
    List<ChatSearchMatch> matches = ChatSearchMatcher.matches(value, query);
    rewrite(text, query);
    if (matches.isEmpty()) {
      return List.of(text);
    }
    List<Component> parts = new ArrayList<>();
    int cursor = 0;
    for (ChatSearchMatch match : matches) {
      if (match.start() > cursor) {
        parts.add(slice(text, value.substring(cursor, match.start()), false));
      }
      if (match.end() > match.start()) {
        parts.add(slice(text, value.substring(match.start(), match.end()), true));
      }
      cursor = match.end();
    }
    if (cursor < value.length()) {
      parts.add(slice(text, value.substring(cursor), false));
    }
    if (parts.isEmpty()) {
      return List.of(text);
    }
    Component last = parts.get(parts.size() - 1);
    last.setChildren(text.getChildren());
    text.setChildren(List.of());
    return parts;
  }

  private static TextComponent slice(TextComponent source, String value, boolean mark) {
    Component copied = source.copy();
    if (!(copied instanceof TextComponent text)) {
      TextComponent fallback = Component.text(value);
      return mark ? mark(fallback) : fallback;
    }
    text.text(value);
    text.setChildren(new ArrayList<>());
    return mark ? mark(text) : text;
  }

  private static TextComponent mark(TextComponent text) {
    text.color(NamedTextColor.YELLOW);
    text.decorate(TextDecoration.UNDERLINED);
    return text;
  }
}
