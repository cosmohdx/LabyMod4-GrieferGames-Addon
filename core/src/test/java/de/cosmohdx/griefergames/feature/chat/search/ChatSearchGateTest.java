package de.cosmohdx.griefergames.feature.chat.search;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ChatSearchGateTest {

  @Test
  void recordsOnlyWhenEveryParentSwitchAllowsIt() {
    assertTrue(ChatSearchGate.shouldRecord(true, true, true, true, true, true));
    assertFalse(ChatSearchGate.shouldRecord(false, true, true, true, true, true));
    assertFalse(ChatSearchGate.shouldRecord(true, false, true, true, true, true));
    assertFalse(ChatSearchGate.shouldRecord(true, true, false, true, true, true));
    assertFalse(ChatSearchGate.shouldRecord(true, true, true, true, false, true));
    assertTrue(ChatSearchGate.shouldRecord(true, true, true, false, false, true));
    assertFalse(ChatSearchGate.shouldRecord(true, true, true, true, true, false));
  }
}
