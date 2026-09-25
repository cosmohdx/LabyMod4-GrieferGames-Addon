package de.cosmohdx.griefergames.feature.chat;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.cosmohdx.griefergames.feature.chat.SecondChatTabSelector.Action;
import de.cosmohdx.griefergames.feature.chat.SecondChatTabSelector.Candidate;
import de.cosmohdx.griefergames.feature.chat.SecondChatTabSelector.Decision;
import java.util.List;
import org.junit.jupiter.api.Test;

class SecondChatTabSelectorTest {

  @Test
  void reusesTheTabWithTheStoredIdWhenTheDisplayNameChanged() {
    List<Candidate> tabs = List.of(
        candidate("other", "2nd Chat", false, false),
        candidate("kept", "Umbenannt", true, false)
    );

    Decision decision = SecondChatTabSelector.select(tabs, "kept", "Nebenchat", "2nd Chat", true);

    assertEquals(Action.REUSE, decision.action());
    assertEquals(1, decision.index());
  }

  @Test
  void doesNotAdoptADifferentTabWhenTheStoredTabIsGone() {
    List<Candidate> tabs = List.of(candidate("other", "Nebenchat", false, false));

    Decision decision = SecondChatTabSelector.select(tabs, "missing", "Nebenchat", null, true);

    assertEquals(Action.CREATE, decision.action());
  }

  @Test
  void keepsTheStoredTabMissingWhenCreationIsDisabled() {
    List<Candidate> tabs = List.of(candidate("other", "Nebenchat", false, false));

    Decision decision = SecondChatTabSelector.select(tabs, "missing", "Nebenchat", null, false);

    assertEquals(Action.NONE, decision.action());
  }

  @Test
  void adoptsAMarkedTabPersistedByLabyModBeforeTheIdWasStored() {
    List<Candidate> tabs = List.of(
        candidate("server", "", false, false),
        candidate("marked", "Alter Name", true, false)
    );

    Decision decision = SecondChatTabSelector.select(tabs, "", "Nebenchat", null, true);

    assertEquals(Action.REUSE, decision.action());
    assertEquals(1, decision.index());
  }

  @Test
  void adoptsTheLegacyFilterTabBeforeANameMatch() {
    List<Candidate> tabs = List.of(
        candidate("named", "2nd Chat", false, false),
        candidate("legacy", "Sonstiges", false, true)
    );

    Decision decision = SecondChatTabSelector.select(tabs, "  ", "2nd Chat", null, true);

    assertEquals(Action.REUSE, decision.action());
    assertEquals(1, decision.index());
  }

  @Test
  void adoptsAnExistingTabByItsStoredName() {
    List<Candidate> tabs = List.of(
        candidate("server", "", false, false),
        candidate("ours", "  nebenchat ", false, false)
    );

    Decision decision = SecondChatTabSelector.select(tabs, null, "Nebenchat", null, true);

    assertEquals(Action.REUSE, decision.action());
    assertEquals(1, decision.index());
  }

  @Test
  void adoptsThePreviousNameWhenTheSettingWasJustRenamed() {
    List<Candidate> tabs = List.of(candidate("ours", "2nd Chat", false, false));

    Decision decision = SecondChatTabSelector.select(tabs, "", "Nachrichten", "2nd Chat", true);

    assertEquals(Action.REUSE, decision.action());
    assertEquals(0, decision.index());
  }

  @Test
  void doesNotTreatABlankNameAsTheServerTab() {
    List<Candidate> tabs = List.of(candidate("server", "   ", false, false));

    Decision decision = SecondChatTabSelector.select(tabs, "", "2nd Chat", "", true);

    assertEquals(Action.CREATE, decision.action());
  }

  @Test
  void doesNotCreateAnotherTabWhenCreationIsDisabled() {
    Decision decision = SecondChatTabSelector.select(List.of(), "", "2nd Chat", null, false);

    assertEquals(Action.NONE, decision.action());
  }

  @Test
  void createsATabOnlyWhenNothingMatches() {
    Decision decision = SecondChatTabSelector.select(
        List.of(candidate("server", "GrieferGames", false, false)),
        "",
        "2nd Chat",
        null,
        true
    );

    assertEquals(Action.CREATE, decision.action());
  }

  private static Candidate candidate(
      String id,
      String storedName,
      boolean marked,
      boolean legacyFilter
  ) {
    return new Candidate(id, storedName, marked, legacyFilter);
  }
}
