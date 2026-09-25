package de.cosmohdx.griefergames.feature.chat;

/**
 * Sends a chat line to the second chat when an enabled category matches it.
 *
 * <p>Routing still requires the chat feature and the second chat to be on. A match uses
 * {@code setSecondChat(true)}, which removes the line from the main chat unless another module
 * asked to keep it there.
 */
public final class SecondChatRouter {

  private final SecondChatCategoriesConfig categories;

  public SecondChatRouter(SecondChatCategoriesConfig categories) {
    this.categories = categories;
  }

  public boolean shouldMoveToSecondChat(
      String plainText,
      boolean chatEnabled,
      boolean secondChatEnabled) {
    if (!chatEnabled || !secondChatEnabled) {
      return false;
    }
    for (SecondChatCategoryConfig category : this.categories.entries()) {
      if (category.routes(plainText)) {
        return true;
      }
    }
    return false;
  }
}
