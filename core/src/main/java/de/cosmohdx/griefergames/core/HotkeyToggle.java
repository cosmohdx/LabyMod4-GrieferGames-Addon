package de.cosmohdx.griefergames.core;

import java.util.function.Consumer;

/**
 * Shared press handling for a feature that is either toggled or held.
 *
 * <p>A press flips the state in {@link Activation#TOGGLE}. In {@link Activation#HOLD} the
 * feature stays on only while the key is down. An optional listener is told when the
 * state actually changes, so a feature can show a message without reading the key event
 * itself.
 */
public final class HotkeyToggle {

  public enum Activation {
    TOGGLE,
    HOLD
  }

  private boolean active;

  public boolean isActive() {
    return this.active;
  }

  public void reset() {
    this.active = false;
  }

  /**
   * @param pressed {@code true} for a new press, {@code false} for a release
   * @return whether {@link #isActive()} changed
   */
  public boolean apply(Activation activation, boolean pressed) {
    return this.apply(activation, pressed, false, null);
  }

  /**
   * @param notify when {@code true} and the state changes, {@code onChange} receives the new state
   * @return whether {@link #isActive()} changed
   */
  public boolean apply(Activation activation, boolean pressed, boolean notify, Consumer<Boolean> onChange) {
    if (activation == null) {
      return false;
    }
    boolean changed = activation == Activation.HOLD
        ? this.applyHold(pressed)
        : this.applyToggle(pressed);
    if (changed && notify && onChange != null) {
      onChange.accept(this.active);
    }
    return changed;
  }

  private boolean applyHold(boolean pressed) {
    if (this.active == pressed) {
      return false;
    }
    this.active = pressed;
    return true;
  }

  private boolean applyToggle(boolean pressed) {
    if (!pressed) {
      return false;
    }
    this.active = !this.active;
    return true;
  }
}
