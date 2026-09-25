package de.cosmohdx.griefergames.core;

import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.TextComponent;
import org.jetbrains.annotations.NotNull;
import java.util.Arrays;

public enum CloudRegionType {
  CITYBUILD("cb1-"),
  FARM("farm-"),
  JAIL("jail-"),
  MINIGAME("minigame-"),
  EVENT("event-");

  private final String prefix;

  CloudRegionType(String prefix) {
    this.prefix = prefix;
  }

  public String getPrefix() {
    return prefix;
  }

  public String onlyName(String text) {
    return text.replace(prefix, "");
  }

  /**
   * Checks if the string contains a server name prefix
   * @param text Text to check
   * @return contains server prefix
   */
  public static boolean containsServerName(String text) {
    return Arrays.stream(CloudRegionType.values()).anyMatch(it -> text.contains(it.getPrefix()));
  }

  /**
   * Region token inside plain header text, for example {@code cb1-1} in a longer line.
   *
   * @param text plain header text
   * @return server name or null
   */
  public static String extractServerName(String text) {
    if (text == null || !containsServerName(text)) {
      return null;
    }
    if (text.contains(" ")) {
      for (String part : text.split(" ")) {
        if (containsServerName(part)) {
          return part;
        }
      }
    }
    return text;
  }

  /**
   * Extracts the server name from a component.
   *
   * @param component component
   * @return server name or null
   */
  public static String extractServerNameFromComponent(@NotNull Component component) {
    if (component.getChildren().size() > 0) {
      for (Component child : component.getChildren()) {
        String serverName = extractServerNameFromComponent(child);
        if (serverName != null) {
          return serverName;
        }
      }
    } else if (component instanceof TextComponent) {
      return extractServerName(((TextComponent) component).getText());
    }
    return null;
  }

  public static CloudRegionType getRegionType(String serverName) {
    return Arrays.stream(CloudRegionType.values()).filter(it -> serverName.startsWith(it.getPrefix())).findFirst().orElse(null);
  }

}
