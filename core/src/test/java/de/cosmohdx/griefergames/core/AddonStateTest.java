package de.cosmohdx.griefergames.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class AddonStateTest {

  @Test
  void networkStartsUnknownAndStaysUnknownUntilDetected() {
    AddonState state = new AddonState();

    assertEquals(SubServerType.UNKNOWN, state.getSubServerType());
    assertFalse(state.isNetworkKnown());
    assertFalse(state.isLegacyNetwork());
    assertFalse(state.isCloudNetwork());
    assertFalse(state.isSubServerType(SubServerType.REGULAR));
    assertFalse(state.isSubServerType(SubServerType.CLOUD));

    state.setSubServerType(null);
    assertEquals(SubServerType.UNKNOWN, state.getSubServerType());
  }

  @Test
  void legacyAndCloudHelpersFollowTheDetectedType() {
    AddonState state = new AddonState();

    state.setSubServerType(SubServerType.REGULAR);
    assertTrue(state.isLegacyNetwork());
    assertTrue(state.isNetworkKnown());
    assertFalse(state.isCloudNetwork());
    assertTrue(state.isSubServerType(SubServerType.REGULAR));

    state.setSubServerType(SubServerType.CLOUD);
    assertTrue(state.isCloudNetwork());
    assertTrue(state.isNetworkKnown());
    assertFalse(state.isLegacyNetwork());
    assertFalse(state.isSubServerType(SubServerType.REGULAR));
  }
}
