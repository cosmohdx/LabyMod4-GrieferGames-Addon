package de.cosmohdx.griefergames.feature.blockoftheday;

import de.cosmohdx.griefergames.GrieferGames;
import de.cosmohdx.griefergames.payload.model.BlockOfTheDayPayload;
import de.cosmohdx.griefergames.payload.model.LootTableProgressPayload;
import java.util.Locale;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.network.server.ServerDisconnectEvent;

public class BlockOfTheDay {

  private String targetKey;
  private String materialName = "";
  private int collected;

  public BlockOfTheDay(GrieferGames griefergames) {
    griefergames.payloads().subscribe(BlockOfTheDayPayload.class, this::onBlock);
    griefergames.payloads().subscribe(LootTableProgressPayload.class, this::onProgress);
  }

  public boolean known() {
    return !this.materialName.isBlank();
  }

  public String hudValue() {
    return this.materialName + " (" + this.collected + ")";
  }

  @Subscribe
  public void onServerQuit(ServerDisconnectEvent event) {
    this.targetKey = null;
    this.materialName = "";
    this.collected = 0;
  }

  private void onBlock(BlockOfTheDayPayload payload) {
    String key = payload.type() + "\0" + payload.blockMaterial() + "\0" + payload.blockData() + "\0" + payload.entityType();
    if (this.targetKey != null && !this.targetKey.equals(key)) {
      this.collected = 0;
    }
    this.targetKey = key;
    this.materialName = displayName(payload);
  }

  private void onProgress(LootTableProgressPayload payload) {
    this.collected++;
  }

  private static String displayName(BlockOfTheDayPayload payload) {
    String raw = payload.entity() ? payload.entityType() : payload.blockMaterial();
    if (raw == null || raw.isBlank()) {
      raw = payload.entity() ? payload.blockMaterial() : payload.entityType();
    }
    if (raw == null || raw.isBlank()) {
      return "";
    }
    int namespace = raw.indexOf(':');
    if (namespace >= 0 && namespace < raw.length() - 1) {
      raw = raw.substring(namespace + 1);
    }
    String[] parts = raw.toLowerCase(Locale.ROOT).split("_");
    StringBuilder name = new StringBuilder();
    for (String part : parts) {
      if (part.isEmpty()) {
        continue;
      }
      if (!name.isEmpty()) {
        name.append(' ');
      }
      name.append(Character.toUpperCase(part.charAt(0)));
      if (part.length() > 1) {
        name.append(part.substring(1));
      }
    }
    return name.toString();
  }
}
