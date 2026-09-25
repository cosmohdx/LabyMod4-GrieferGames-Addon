package de.cosmohdx.griefergames.feature.chat;

import net.labymod.api.Laby;
import net.labymod.api.client.component.Component;

public abstract class ChatModule {
  public String getPlainText(Component component) {
    StringBuilder builder = new StringBuilder();
    Laby.references().componentRenderer().getColorStrippingFlattener().flatten(component, builder::append);
    return builder.toString();
  }

  public String getFormattedText(Component component) {
    return Laby.references().componentRenderer().legacySectionSerializer().serialize(component);
  }
}
