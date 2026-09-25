package de.cosmohdx.griefergames.feature.chat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.cosmohdx.griefergames.core.GrieferGamesConfig;
import org.junit.jupiter.api.Test;

class ChatTimeMarkersTest {

  @Test
  void defaultFormatMatchesTheTimestampChild() {
    String format = GrieferGamesConfig.DEFAULT_CHATTIME_FORMAT;
    assertTrue(ChatTimeMarkers.isLeadingStamp("[12:00:00] ", format));
    assertTrue(ChatTimeMarkers.isLeadingStamp("[09:05:07]", format));
    assertFalse(ChatTimeMarkers.isLeadingStamp("Supreme+ ┃ Notch » Hallo", format));
    assertFalse(ChatTimeMarkers.isLeadingStamp("[12:00:00] extra", format));
  }

  @Test
  void customFormatIsRecognised() {
    assertTrue(ChatTimeMarkers.isLeadingStamp("09:05:07 ", "{h}:{m}:{s}"));
    assertFalse(ChatTimeMarkers.isLeadingStamp("[09:05:07]", "{h}:{m}:{s}"));
  }

  @Test
  void headSitsBehindAnExistingTimestamp() {
    assertEquals(1, ChatTimeMarkers.insertionIndex(true, true));
    assertEquals(0, ChatTimeMarkers.insertionIndex(true, false));
    assertEquals(0, ChatTimeMarkers.insertionIndex(false, true));
    assertEquals(0, ChatTimeMarkers.insertionIndex(false, false));
  }
}
