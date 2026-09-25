package de.cosmohdx.griefergames.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.cosmohdx.griefergames.core.HotkeyToggle.Activation;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class HotkeyToggleTest {

  @Test
  void toggleFlipsOnPressAndIgnoresRelease() {
    HotkeyToggle hotkey = new HotkeyToggle();

    assertTrue(hotkey.apply(Activation.TOGGLE, true));
    assertTrue(hotkey.isActive());
    assertFalse(hotkey.apply(Activation.TOGGLE, false));
    assertTrue(hotkey.isActive());
    assertTrue(hotkey.apply(Activation.TOGGLE, true));
    assertFalse(hotkey.isActive());
  }

  @Test
  void holdFollowsTheKey() {
    HotkeyToggle hotkey = new HotkeyToggle();

    assertTrue(hotkey.apply(Activation.HOLD, true));
    assertTrue(hotkey.isActive());
    assertFalse(hotkey.apply(Activation.HOLD, true));
    assertTrue(hotkey.apply(Activation.HOLD, false));
    assertFalse(hotkey.isActive());
    assertFalse(hotkey.apply(Activation.HOLD, false));
  }

  @Test
  void notificationRunsOnlyWhenTheStateChanges() {
    HotkeyToggle hotkey = new HotkeyToggle();
    List<Boolean> notices = new ArrayList<>();

    hotkey.apply(Activation.TOGGLE, true, true, notices::add);
    hotkey.apply(Activation.TOGGLE, false, true, notices::add);
    hotkey.apply(Activation.TOGGLE, true, false, notices::add);

    assertEquals(List.of(true), notices);
    assertFalse(hotkey.isActive());
  }

  @Test
  void resetClearsAHeldKey() {
    HotkeyToggle hotkey = new HotkeyToggle();
    hotkey.apply(Activation.HOLD, true);

    hotkey.reset();

    assertFalse(hotkey.isActive());
  }
}
