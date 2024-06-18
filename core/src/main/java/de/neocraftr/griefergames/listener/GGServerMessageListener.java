package de.neocraftr.griefergames.listener;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.neocraftr.griefergames.GrieferGames;
import net.labymod.api.Laby;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.serializer.legacy.LegacyComponentSerializer;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.network.server.NetworkPayloadEvent;
import net.labymod.api.event.client.network.server.NetworkPayloadEvent.Side;
import net.labymod.core.main.LabyMod;
import net.labymod.serverapi.api.model.component.ServerAPIComponent;
import net.labymod.serverapi.api.payload.io.PayloadReader;
import net.labymod.serverapi.core.model.display.Subtitle;

import java.util.UUID;

public class GGServerMessageListener {
  private final GrieferGames griefergames;
  private final Gson gson = new Gson();

  public GGServerMessageListener(GrieferGames griefergames) {
    this.griefergames = griefergames;
  }

  @Subscribe
  public void onServerMessage(NetworkPayloadEvent event) {
    if(!griefergames.isOnGrieferGames()) return;

    if(event.side() == Side.RECEIVE &&
        event.identifier().getNamespace().equals("mysterymod") &&
        event.identifier().getPath().equals("mm")) {

      PayloadReader reader = new PayloadReader(event.getPayload());
      String messageKey = reader.readString();
      String messageJson = reader.readString();

      //System.out.println(messageKey+" "+messageJson);

      JsonElement message;
      try {
        message = gson.fromJson(messageJson, JsonElement.class);
      } catch(Exception e) {
        System.err.println("Exception while parsing MysteryMod message: exception="+e.getMessage()+", message="+messageJson);
        return;
      }

      if(messageKey.equals("redstone")) {
        String redstoneState = message.getAsJsonObject().get("status").getAsString();
        griefergames.setRedstoneActive(redstoneState.equals("0"));
      }

      if(messageKey.equals("user_subtitle")) {
        JsonObject subtitleData = message.getAsJsonArray().get(0).getAsJsonObject();

        String text = Laby.labyAPI().minecraft().componentMapper().translateColorCodes(subtitleData.get("text").getAsString());

        Subtitle subtitle = Subtitle.create(UUID.fromString(subtitleData.get("targetId").getAsString()), ServerAPIComponent.text(text), 1.2);
        LabyMod.references().subtitleService().addSubtitle(subtitle);
      }
    }
  }
}
