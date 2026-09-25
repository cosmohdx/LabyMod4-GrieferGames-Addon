package de.cosmohdx.griefergames.feature.chat;

import java.util.List;
import net.labymod.api.configuration.loader.Config;

/**
 * Built-in message categories for the second chat.
 *
 * <p>Each entry is a {@link SecondChatCategoryConfig}: a toggle plus a pattern list.
 * {@link #entries()} is the list a later router can walk without knowing every field.
 */
public class SecondChatCategoriesConfig extends Config {

  private final PrivateMessageCategoryConfig privateMessages = new PrivateMessageCategoryConfig();
  private final SecondChatCategoryConfig plotChat = new SecondChatCategoryConfig("plotChat", true);
  private final SecondChatCategoryConfig remover = new SecondChatCategoryConfig("remover", false);
  private final SecondChatCategoryConfig payments = new SecondChatCategoryConfig("payments", true);
  private final SecondChatCategoryConfig bank = new SecondChatCategoryConfig("bank", true);

  public PrivateMessageCategoryConfig privateMessages() {
    return bind(this.privateMessages, "privateMessages");
  }

  public SecondChatCategoryConfig plotChat() {
    return bind(this.plotChat, "plotChat");
  }

  public SecondChatCategoryConfig remover() {
    return bind(this.remover, "remover");
  }

  public SecondChatCategoryConfig payments() {
    return bind(this.payments, "payments");
  }

  public SecondChatCategoryConfig bank() {
    return bind(this.bank, "bank");
  }

  public List<SecondChatCategoryConfig> entries() {
    return List.of(privateMessages(), plotChat(), remover(), payments(), bank());
  }

  private static <T extends SecondChatCategoryConfig> T bind(T category, String id) {
    category.bindId(id);
    return category;
  }
}
