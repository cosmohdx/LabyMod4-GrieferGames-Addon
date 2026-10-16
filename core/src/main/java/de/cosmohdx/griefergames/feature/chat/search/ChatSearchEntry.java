package de.cosmohdx.griefergames.feature.chat.search;

import java.util.Objects;

/**
 * One recorded chat line. The component is stored as Adventure Gson JSON so colors,
 * hover and click events survive a round-trip.
 */
public final class ChatSearchEntry {

  private final long timestamp;
  private final ChatSearchTab tab;
  private final String subServer;
  private final String componentJson;
  private final String plainText;

  public ChatSearchEntry(
      long timestamp,
      ChatSearchTab tab,
      String subServer,
      String componentJson,
      String plainText) {
    this.timestamp = timestamp;
    this.tab = tab == null ? ChatSearchTab.MAIN : tab;
    this.subServer = subServer == null ? "" : subServer;
    this.componentJson = componentJson == null ? "" : componentJson;
    this.plainText = plainText == null ? "" : plainText;
  }

  public long timestamp() {
    return this.timestamp;
  }

  public ChatSearchTab tab() {
    return this.tab;
  }

  public String subServer() {
    return this.subServer;
  }

  public String componentJson() {
    return this.componentJson;
  }

  public String plainText() {
    return this.plainText;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof ChatSearchEntry that)) {
      return false;
    }
    return this.timestamp == that.timestamp
        && this.tab == that.tab
        && Objects.equals(this.subServer, that.subServer)
        && Objects.equals(this.componentJson, that.componentJson)
        && Objects.equals(this.plainText, that.plainText);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.timestamp, this.tab, this.subServer, this.componentJson, this.plainText);
  }
}
