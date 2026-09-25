package de.cosmohdx.griefergames.feature.subserver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.cosmohdx.griefergames.core.SubServerType;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class NetworkTypeUpdaterTest {

  @Test
  void everyActualChangeProducesAnEvent() {
    assertChange(SubServerType.UNKNOWN, SubServerType.REGULAR);
    assertChange(SubServerType.UNKNOWN, SubServerType.CLOUD);
    assertChange(SubServerType.REGULAR, SubServerType.CLOUD);
    assertChange(SubServerType.CLOUD, SubServerType.REGULAR);
    assertChange(SubServerType.REGULAR, SubServerType.UNKNOWN);
    assertChange(SubServerType.CLOUD, SubServerType.UNKNOWN);
  }

  @Test
  void repeatingTheCurrentTypeDoesNotProduceAnEvent() {
    assertTrue(NetworkTypeUpdater.change(SubServerType.UNKNOWN, SubServerType.UNKNOWN).isEmpty());
    assertTrue(NetworkTypeUpdater.change(SubServerType.REGULAR, SubServerType.REGULAR).isEmpty());
    assertTrue(NetworkTypeUpdater.change(SubServerType.CLOUD, SubServerType.CLOUD).isEmpty());
    assertTrue(NetworkTypeUpdater.change(SubServerType.REGULAR, null).isEmpty());
  }

  private static void assertChange(SubServerType oldType, SubServerType newType) {
    Optional<GGNetworkTypeChangeEvent> event = NetworkTypeUpdater.change(oldType, newType);
    assertTrue(event.isPresent());
    assertEquals(oldType, event.get().oldType());
    assertEquals(newType, event.get().newType());
  }
}
