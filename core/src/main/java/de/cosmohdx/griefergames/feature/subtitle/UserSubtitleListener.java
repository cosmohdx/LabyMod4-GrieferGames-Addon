package de.cosmohdx.griefergames.feature.subtitle;

import de.cosmohdx.griefergames.GrieferGames;
import de.cosmohdx.griefergames.payload.model.UserSubtitlePayload;
import net.labymod.api.Laby;
import net.labymod.serverapi.api.model.component.ServerAPIComponent;
import net.labymod.serverapi.core.model.display.Subtitle;

public class UserSubtitleListener {

  public UserSubtitleListener(GrieferGames griefergames) {
    griefergames.payloads().subscribe(UserSubtitlePayload.class, this::show);
  }

  private void show(UserSubtitlePayload payload) {
    String text = Laby.labyAPI().minecraft().componentMapper().translateColorCodes(payload.text());
    Subtitle.create(payload.targetId(), ServerAPIComponent.text(text), 1.2);
  }
}
