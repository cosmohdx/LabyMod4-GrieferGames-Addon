package de.cosmohdx.griefergames.feature.chat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.Test;

class OutgoingChatTest {

  @Test
  void publicChatCanBeSplitAndGlobalChatCannot() {
    assertEquals(Optional.of("hallo welt"), OutgoingChat.textToSplit("hallo welt", true, Optional.empty()));
    assertEquals(Optional.of("&6hallo"), OutgoingChat.textToSplit("&6hallo", true, Optional.empty()));
    assertTrue(OutgoingChat.textToSplit("@langer text", true, Optional.empty()).isEmpty());
    assertTrue(OutgoingChat.textToSplit("&a&l@langer text", true, Optional.empty()).isEmpty());
    assertTrue(OutgoingChat.textToSplit(".plot", true, Optional.empty()).isEmpty());
    assertTrue(OutgoingChat.textToSplit("-menu", true, Optional.empty()).isEmpty());
  }

  @Test
  void privateMessagesAndReplies() {
    assertEquals(Optional.of("/msg Notch hallo welt"),
        OutgoingChat.textToSplit("/msg Notch hallo welt", true, Optional.empty()));
    assertEquals(Optional.of("/MSG Notch hallo"),
        OutgoingChat.textToSplit("/MSG Notch hallo", true, Optional.empty()));
    assertTrue(OutgoingChat.textToSplit("/msg Notch hallo", false, Optional.of("Notch")).isEmpty());
    assertEquals(Optional.of("/msg ~Notch hallo welt"),
        OutgoingChat.textToSplit("/r hallo welt", true, Optional.of("~Notch")));
    assertTrue(OutgoingChat.textToSplit("/r hallo welt", true, Optional.empty()).isEmpty());
    assertTrue(OutgoingChat.textToSplit("/r", true, Optional.of("Notch")).isEmpty());
    assertTrue(OutgoingChat.textToSplit("/pay Notch 1", true, Optional.of("Notch")).isEmpty());
  }
}
