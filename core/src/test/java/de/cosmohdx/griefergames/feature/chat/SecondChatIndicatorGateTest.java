package de.cosmohdx.griefergames.feature.chat;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.cosmohdx.griefergames.feature.chat.SecondChatIndicatorGate.Action;
import org.junit.jupiter.api.Test;

class SecondChatIndicatorGateTest {

  @Test
  void ignoresTabsThatAreNotUsingIndicators() {
    assertEquals(Action.LEAVE, SecondChatIndicatorGate.decide(false, true, true, false, false, false));
    assertEquals(Action.LEAVE, SecondChatIndicatorGate.decide(true, false, true, false, false, false));
  }

  @Test
  void showsMarkedMessagesWhenThisTabHasNoFilters() {
    assertEquals(Action.SHOW, SecondChatIndicatorGate.decide(true, true, true, false, false, false));
  }

  @Test
  void hidesMessagesTheAddonDidNotMove() {
    assertEquals(Action.HIDE, SecondChatIndicatorGate.decide(true, true, false, false, false, false));
    assertEquals(Action.HIDE, SecondChatIndicatorGate.decide(true, true, false, true, true, false));
  }

  @Test
  void ownFiltersStillApplyToMovedMessages() {
    assertEquals(Action.SHOW, SecondChatIndicatorGate.decide(true, true, true, true, true, false));
    assertEquals(Action.HIDE, SecondChatIndicatorGate.decide(true, true, true, true, false, false));
    assertEquals(Action.HIDE, SecondChatIndicatorGate.decide(true, true, true, true, true, true));
  }
}
