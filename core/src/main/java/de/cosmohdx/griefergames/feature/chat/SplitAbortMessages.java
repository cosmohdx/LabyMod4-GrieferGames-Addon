package de.cosmohdx.griefergames.feature.chat;

import java.util.List;
import java.util.Locale;

/**
 * Server lines that should drop the rest of a split message.
 * Player chat is ignored, even when it quotes one of these phrases.
 */
public final class SplitAbortMessages {

  private static final List<String> PHRASES = List.of(
      "fehler: spieler nicht gefunden",
      "spieler wurde nicht gefunden",
      "spieler ist nicht online",
      "werbefilter",
      "enthält werbung",
      "enthaelt werbung",
      "enthält einen link",
      "senden von werbung",
      "senden von links",
      "nicht erlaubte ip",
      "enthält eine ip",
      "gemutet",
      "slowchat",
      "du schreibst zu schnell",
      "bitte warte noch",
      "deine nachricht wurde blockiert"
  );

  private SplitAbortMessages() {
  }

  public static boolean aborts(String plainText) {
    if (plainText == null || plainText.isBlank()) {
      return false;
    }
    if (ChatSenderExtractor.sender(plainText).isPresent()) {
      return false;
    }
    String lower = plainText.toLowerCase(Locale.ROOT);
    for (String phrase : PHRASES) {
      if (lower.contains(phrase)) {
        return true;
      }
    }
    return false;
  }
}
