package de.cosmohdx.griefergames.feature.chat;

import de.cosmohdx.griefergames.GrieferGames;
import net.labymod.api.event.Subscribe;

/**
 * Applies {@link SecondChatRouter} to incoming GrieferGames chat.
 */
public final class SecondChatRouterListener {

  private final GrieferGames griefergames;
  private final SecondChatRouter router;

  public SecondChatRouterListener(GrieferGames griefergames) {
    this.griefergames = griefergames;
    SecondChatCategoriesConfig categories =
        griefergames.configuration().chat().secondChat().categories();
    this.router = new SecondChatRouter(categories);
    InvalidPatternReporter reporter = (categoryId, pattern, error) -> griefergames.logger().warn(
        GrieferGames.LOG_PREFIX + "Second chat category " + categoryId
            + " skipped invalid pattern \"" + pattern + "\": " + error
    );
    for (SecondChatCategoryConfig category : categories.entries()) {
      category.reportInvalidPatternsTo(reporter);
    }
  }

  @Subscribe
  public void onMessage(GGChatProcessEvent event) {
    if (event.isCancelled() || event.isSecondChat() || event.getMessage() == null) {
      return;
    }
    GrieferGamesChatConfig chat = this.griefergames.configuration().chat();
    if (this.router.shouldMoveToSecondChat(
        event.getMessage().getPlainText(),
        chat.isEnabled(),
        chat.secondChat().isEnabled())) {
      event.setSecondChat(true);
    }
  }
}
