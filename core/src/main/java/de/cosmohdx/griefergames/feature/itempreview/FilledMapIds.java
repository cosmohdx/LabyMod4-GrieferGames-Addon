package de.cosmohdx.griefergames.feature.itempreview;

import java.lang.reflect.Method;

public final class FilledMapIds {

  /** Maps store their id as item damage before 1.13. */
  public static final int LEGACY_DAMAGE_PROTOCOL = 393;

  /** The map id data component exists from 1.20.5 onward. */
  public static final int COMPONENT_PROTOCOL = 766;

  private FilledMapIds() {
  }

  public static int resolve(int protocol, int legacyDamage, boolean nbtPresent, int nbtMap, Object component) {
    if (protocol > 0 && protocol < LEGACY_DAMAGE_PROTOCOL) {
      return legacyDamage < 0 ? -1 : legacyDamage;
    }
    if (protocol >= COMPONENT_PROTOCOL || protocol <= 0) {
      int componentId = componentId(component);
      if (componentId >= 0) {
        return componentId;
      }
    }
    if (nbtPresent && nbtMap >= 0) {
      return nbtMap;
    }
    return componentId(component);
  }

  public static int componentId(Object value) {
    if (value instanceof Number number) {
      return nonNegative(number.intValue());
    }
    if (value == null) {
      return -1;
    }
    try {
      Method method = value.getClass().getMethod("id");
      Object result = method.invoke(value);
      if (result instanceof Number number) {
        return nonNegative(number.intValue());
      }
    } catch (ReflectiveOperationException ignored) {
      return -1;
    }
    return -1;
  }

  private static int nonNegative(int id) {
    return id < 0 ? -1 : id;
  }
}
