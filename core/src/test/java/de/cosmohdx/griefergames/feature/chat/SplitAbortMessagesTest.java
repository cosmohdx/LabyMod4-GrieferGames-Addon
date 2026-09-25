package de.cosmohdx.griefergames.feature.chat;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SplitAbortMessagesTest {

  @Test
  void serverErrorsAbortAndQuotedPlayerChatDoesNot() {
    assertTrue(SplitAbortMessages.aborts("Fehler: Spieler nicht gefunden."));
    assertTrue(SplitAbortMessages.aborts("Der Spieler wurde nicht gefunden."));
    assertTrue(SplitAbortMessages.aborts("Deine Nachricht wurde vom Werbefilter blockiert."));
    assertTrue(SplitAbortMessages.aborts("Deine Nachricht enthält eine nicht erlaubte IP-Adresse."));
    assertTrue(SplitAbortMessages.aborts("Du bist noch für 5 Minuten gemutet."));
    assertTrue(SplitAbortMessages.aborts("Bitte warte noch einen Moment, bevor du eine weitere Nachricht sendest."));
    assertFalse(SplitAbortMessages.aborts(
        "Supreme+ ┃ Notch » Fehler: Spieler nicht gefunden."));
    assertFalse(SplitAbortMessages.aborts("Supreme+ ┃ Notch » Hallo"));
    assertFalse(SplitAbortMessages.aborts(""));
  }
}
