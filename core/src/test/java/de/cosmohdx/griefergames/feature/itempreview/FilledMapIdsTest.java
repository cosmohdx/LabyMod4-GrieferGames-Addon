package de.cosmohdx.griefergames.feature.itempreview;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class FilledMapIdsTest {

  @Test
  void usesItemDamageOnLegacyProtocols() {
    assertEquals(12, FilledMapIds.resolve(47, 12, true, 3, 9));
    assertEquals(0, FilledMapIds.resolve(340, 0, false, -1, null));
  }

  @Test
  void usesTheNbtIdBetween113And1204() {
    assertEquals(15, FilledMapIds.resolve(765, 0, true, 15, 4));
    assertEquals(-1, FilledMapIds.resolve(765, 0, false, -1, null));
  }

  @Test
  void prefersTheComponentIdFrom1205Onward() {
    assertEquals(8, FilledMapIds.resolve(767, 0, true, 3, 8));
    assertEquals(3, FilledMapIds.resolve(767, 0, true, 3, null));
  }

  @Test
  void readsTheIdMethodOfAMapIdValue() {
    assertEquals(6, FilledMapIds.componentId(6));
    assertEquals(4, FilledMapIds.componentId(new MapId(4)));
    assertEquals(-1, FilledMapIds.componentId(null));
    assertEquals(-1, FilledMapIds.componentId("map"));
    assertEquals(-1, FilledMapIds.componentId(-2));
  }

  private record MapId(int id) {
  }
}
