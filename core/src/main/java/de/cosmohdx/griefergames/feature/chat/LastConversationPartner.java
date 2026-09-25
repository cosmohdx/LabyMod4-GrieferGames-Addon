package de.cosmohdx.griefergames.feature.chat;

import java.util.Optional;

/**
 * Last player from an incoming or outgoing private message, used to turn {@code /r} into
 * {@code /msg}. The name is the one already shown in chat, nick prefix included.
 */
public final class LastConversationPartner {

  private String name;

  public void remember(String name) {
    if (name == null || name.isBlank()) {
      return;
    }
    this.name = name;
  }

  public Optional<String> name() {
    return Optional.ofNullable(this.name);
  }

  public void clear() {
    this.name = null;
  }
}
