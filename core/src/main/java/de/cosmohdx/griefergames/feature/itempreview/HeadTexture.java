package de.cosmohdx.griefergames.feature.itempreview;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public final class HeadTexture {

  private final String name;
  private final UUID uuid;
  private final String url;
  private final boolean slim;

  public HeadTexture(String name, UUID uuid, String url, boolean slim) {
    this.name = name == null || name.isEmpty() ? null : name;
    this.uuid = uuid;
    this.url = url == null || url.isEmpty() ? null : url;
    this.slim = slim;
  }

  public String name() {
    return this.name;
  }

  public UUID uuid() {
    return this.uuid;
  }

  public String url() {
    return this.url;
  }

  public boolean slim() {
    return this.slim;
  }

  public boolean canRender() {
    return this.url != null || this.uuid != null || this.name != null;
  }

  /**
   * Stable id for a custom texture. Version 3 so it does not collide with online profile ids.
   */
  public UUID textureId() {
    if (this.url != null) {
      return UUID.nameUUIDFromBytes(this.url.getBytes(StandardCharsets.UTF_8));
    }
    return this.uuid;
  }
}
