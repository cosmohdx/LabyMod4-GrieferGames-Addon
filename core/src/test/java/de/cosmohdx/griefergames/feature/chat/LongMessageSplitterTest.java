package de.cosmohdx.griefergames.feature.chat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.cosmohdx.griefergames.feature.chat.LongMessageSplitter.Parts;
import java.util.List;
import org.junit.jupiter.api.Test;

class LongMessageSplitterTest {

  @Test
  void exactlyOneHundredCharactersStaysOnePart() {
    String text = "a".repeat(100);
    assertEquals(List.of(text), parts(text, 100, 3));
  }

  @Test
  void oneHundredOneCharactersSplitOnTheSpace() {
    String text = "a".repeat(50) + " " + "b".repeat(50);
    assertEquals(List.of("a".repeat(50), "b".repeat(50)), parts(text, 100, 3));
    assertInstanceOf(LongMessageSplitter.WordTooLong.class,
        LongMessageSplitter.split("a".repeat(101), 100, 3));
  }

  @Test
  void messagePrefixCountsTowardTheLimitAndIsRepeated() {
    String text = "/msg Notch " + "a".repeat(50) + " " + "b".repeat(50);
    assertEquals(
        List.of("/msg Notch " + "a".repeat(50), "/msg Notch " + "b".repeat(50)),
        parts(text, 100, 3));
  }

  @Test
  void activeColorIsRepeatedOnTheNextPart() {
    assertEquals(List.of("&6hello", "&6world"), parts("&6hello world", 10, 3));
    assertEquals(List.of("&6hello", "&bworld"), parts("&6hello &bworld", 10, 3));
    assertEquals(List.of("&6hello", "&6&lworld"), parts("&6hello &lworld", 10, 3));
    assertEquals(List.of("&rhello", "&rworld"), parts("&rhello world", 7, 3));
  }

  @Test
  void aTrailingAmpersandStaysAttachedToItsWord() {
    assertEquals(List.of("&6hello&", "&6world"), parts("&6hello& world", 8, 3));
    assertEquals(List.of("hello", "&"), parts("hello &", 5, 3));
  }

  @Test
  void aWordOrLinkLongerThanTheLimitIsRefused() {
    String link = "https://example.com/" + "a".repeat(90);
    assertInstanceOf(LongMessageSplitter.WordTooLong.class,
        LongMessageSplitter.split("schau " + link, 100, 3));
    assertEquals(List.of("schau https://example.com"), parts("schau https://example.com", 100, 3));
  }

  @Test
  void tooManyPartsAreRefused() {
    assertInstanceOf(LongMessageSplitter.TooLong.class,
        LongMessageSplitter.split("aa bb cc dd", 2, 3));
    assertEquals(List.of("aa", "bb", "cc"), parts("aa bb cc", 2, 3));
  }

  @Test
  void multibyteCharactersCountAsOneAndStayIntact() {
    String emoji = "\uD83D\uDE00";
    assertEquals(1, LongMessageSplitter.length(emoji));
    assertEquals(List.of(emoji.repeat(100)), parts(emoji.repeat(100), 100, 3));
    assertInstanceOf(LongMessageSplitter.WordTooLong.class,
        LongMessageSplitter.split(emoji.repeat(101), 100, 3));
    assertEquals(List.of(emoji.repeat(60), emoji.repeat(60)),
        parts(emoji.repeat(60) + " " + emoji.repeat(60), 100, 3));
  }

  private static List<String> parts(String text, int limit, int maxParts) {
    LongMessageSplitter.SplitResult result = LongMessageSplitter.split(text, limit, maxParts);
    Parts parts = assertInstanceOf(Parts.class, result);
    for (String part : parts.parts()) {
      assertTrue(LongMessageSplitter.length(part) <= limit, part);
    }
    return parts.parts();
  }
}
