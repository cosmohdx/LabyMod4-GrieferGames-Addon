package de.cosmohdx.griefergames.feature.chat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class SecondChatRouterTest {

  @Test
  void registryAndConfigEntriesStayInTheSameOrder() {
    SecondChatCategoriesConfig categories = new SecondChatCategoriesConfig();
    List<String> ids = categories.entries().stream().map(SecondChatCategoryConfig::id).toList();

    assertEquals(SecondChatCategory.ids(), ids);
  }

  @Test
  void builtInPatternsMatchTheMessagesTheOldDetectorsHandled() {
    SecondChatRouter router = router();

    assertRoutes(router, "[Supreme+ ┃ ~Notch -> mir] Hallo");
    assertRoutes(router, "[Supreme+ ┃ !Notch -> me] Hello");
    assertRoutes(router, "§6[Co-Owner ┃ Notch -> mir] Hallo");
    assertRoutes(router, "[mir -> Supreme+ ┃ Notch] Hallo zurück");
    assertRoutes(router, "[me -> Co-Owner ┃ !Notch] Hello");
    assertFalse(router.shouldMoveToSecondChat(
        "Supreme+ ┃ Notch » [Supreme+ ┃ Notch -> mir] fake", true, true));

    assertRoutes(router, "[Plot-Chat] Notch: Hallo auf dem Plot");
    assertFalse(router.shouldMoveToSecondChat("Plot-Chat ohne Klammern", true, true));

    assertRoutes(router, "Supreme+ ┃ Notch hat dir $1,234.50 gegeben.");
    assertRoutes(router, "Du hast Co-Owner ┃ ~Notch $50 gegeben.");
    assertRoutes(router, "$1,250.50 wurde zu deinem Konto hinzugefügt.");
    assertRoutes(router, "Kontostand: $1,000");
    assertFalse(router.shouldMoveToSecondChat("Kontostand ohne Doppelpunkt", true, true));

    assertRoutes(router, "[Bank] Du hast $500 auf dein Bankkonto eingezahlt.");
    assertRoutes(router, "[Bank] Du hast $500 von deinem Bankkonto abgehoben.");
    assertFalse(router.shouldMoveToSecondChat("[Bank]ohne Leerzeichen", true, true));

    String itemWarning =
        "[GrieferGames] Warnung! Die auf dem Boden liegenden Items werden in 30 Sekunden entfernt!";
    assertFalse(router.shouldMoveToSecondChat(itemWarning, true, true));

    SecondChatCategoriesConfig withRemover = new SecondChatCategoriesConfig();
    withRemover.remover().showInSecondChatProperty().set(true);
    SecondChatRouter removerRouter = new SecondChatRouter(withRemover);
    assertRoutes(removerRouter, itemWarning);
    assertRoutes(removerRouter,
        "[GrieferGames] Es wurden 12 auf dem Boden liegende Items entfernt!");
    assertRoutes(removerRouter, "[MobRemover] Achtung! In 1 Minute werden alle Tiere gelöscht.");
    assertRoutes(removerRouter, "[MobRemover] Achtung! In 5 Minuten werden alle Tiere gelöscht.");
    assertRoutes(removerRouter, "[MobRemover] Es wurden 3 Tiere entfernt.");
  }

  @Test
  void disabledCategoryAndParentSwitchesKeepTheMessageInTheMainChat() {
    SecondChatCategoriesConfig categories = new SecondChatCategoriesConfig();
    SecondChatRouter router = new SecondChatRouter(categories);
    String plot = "[Plot-Chat] Notch: Hallo";

    assertTrue(router.shouldMoveToSecondChat(plot, true, true));
    assertFalse(router.shouldMoveToSecondChat(plot, false, true));
    assertFalse(router.shouldMoveToSecondChat(plot, true, false));

    categories.plotChat().showInSecondChatProperty().set(false);
    assertFalse(router.shouldMoveToSecondChat(plot, true, true));
    assertFalse(categories.plotChat().routes(plot));
    assertTrue(categories.plotChat().matchesPattern(plot));
  }

  @Test
  void userPatternsAreAddedAndRecompiledWithoutDroppingBuiltIns() {
    SecondChatCategoriesConfig categories = new SecondChatCategoriesConfig();
    SecondChatRouter router = new SecondChatRouter(categories);
    String custom = "Clan-Hinweis: Treffen um 20 Uhr";

    assertFalse(router.shouldMoveToSecondChat(custom, true, true));
    categories.plotChat().patterns().add("Clan-Hinweis:.*");
    assertTrue(router.shouldMoveToSecondChat(custom, true, true));
    assertTrue(router.shouldMoveToSecondChat("[Plot-Chat] bleibt", true, true));

    categories.plotChat().patterns().add("[");
    assertTrue(router.shouldMoveToSecondChat("[Plot-Chat] bleibt", true, true));
    assertTrue(categories.plotChat().invalidPatternHint().contains("Unclosed character class"));

    categories.plotChat().showInSecondChatProperty().set(false);
    assertFalse(router.shouldMoveToSecondChat(custom, true, true));
  }

  @Test
  void emptyListsReceiveDefaultsOnceAndEditedListsStayEdited() {
    SecondChatCategoriesConfig categories = new SecondChatCategoriesConfig();
    SecondChatCategoryConfig plot = categories.plotChat();
    String plotMessage = "[Plot-Chat] Notch: Hallo";

    assertFalse(plot.patterns().isEmpty());
    assertTrue(plot.matchesPattern(plotMessage));

    plot.patterns().getTags().clear();
    assertFalse(new SecondChatRouter(categories).shouldMoveToSecondChat(plotMessage, true, true));

    SecondChatCategoryConfig reloaded = new SecondChatCategoriesConfig().plotChat();
    assertTrue(reloaded.matchesPattern(plotMessage));
    reloaded.patterns().getTags().remove(0);
    assertFalse(reloaded.matchesPattern(plotMessage));
    reloaded.patterns().add("Clan-Hinweis:.*");
    assertTrue(reloaded.matchesPattern("Clan-Hinweis: Treffen"));
  }

  @Test
  void invalidPatternIsReportedOnceUntilTheListChanges() {
    List<String> reports = new ArrayList<>();
    CompiledChatPatterns compiled = new CompiledChatPatterns();
    compiled.reportTo((categoryId, pattern, error) ->
        reports.add(categoryId + " " + pattern + " " + error));
    List<String> sources = List.of("[");

    assertFalse(compiled.matches("plotChat", "text", sources));
    compiled.matches("plotChat", "again", sources);
    assertEquals(1, reports.size());
    assertTrue(reports.get(0).contains("plotChat"));
    assertTrue(reports.get(0).contains("["));
    assertTrue(reports.get(0).contains("Unclosed character class"));
    assertTrue(compiled.hint("plotChat", sources).contains("Unclosed character class"));

    assertTrue(compiled.matches("plotChat", "custom", List.of("custom", "also[")));
    assertEquals(2, reports.size());
    assertTrue(reports.get(1).contains("also["));
    assertEquals("", compiled.hint("plotChat", List.of("custom")));
  }

  private static void assertRoutes(SecondChatRouter router, String message) {
    assertTrue(router.shouldMoveToSecondChat(message, true, true), message);
  }

  private static SecondChatRouter router() {
    return new SecondChatRouter(categories());
  }

  private static SecondChatCategoriesConfig categories() {
    return new SecondChatCategoriesConfig();
  }
}
