package de.cosmohdx.griefergames.feature.kisupport;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

class KiSupportMessageTest {

  private static final Pattern ALFRED_MESSAGE = Pattern.compile(
      "(?i).*KI-Support\\W+Alfred\\s*(?:\\u00BB|»).*");

  @Test
  void detectsAlfredReplyWithAsciiPipe() {
    assertTrue(ALFRED_MESSAGE.matcher(
        "[TEAM] KI-Support | Alfred » SiMaDN3, prüfe, ob es eine Liefer-Aufgabe ist.")
        .matches());
  }

  @Test
  void detectsAlfredReplyWithMissingGlyphSeparator() {
    assertTrue(ALFRED_MESSAGE.matcher(
        "[TEAM] KI-Support \u25AF Alfred » OdinHerkules146, das Fly+ Perk ermöglicht dir das Fliegen.")
        .matches());
    assertTrue(ALFRED_MESSAGE.matcher(
        "[TEAM] KI-Support \u2502 Alfred » OdinHerkules146, das Fly+ Perk ermöglicht dir das Fliegen.")
        .matches());
  }

  @Test
  void ignoresOtherMessages() {
    assertFalse(ALFRED_MESSAGE.matcher(
        "[PiC] Hero | JanyyyXI » bist du in dschungel ?").matches());
    assertFalse(ALFRED_MESSAGE.matcher(
        "[GrieferGames] Du scheinst gerade nicht aktiv zu spielen...").matches());
    assertFalse(ALFRED_MESSAGE.matcher(
        "jemand schreibt über KI-Support und Alfred im Chat").matches());
  }
}
