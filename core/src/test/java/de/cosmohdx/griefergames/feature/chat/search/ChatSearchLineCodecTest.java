package de.cosmohdx.griefergames.feature.chat.search;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ChatSearchLineCodecTest {

  @Test
  void roundTripsEntryFieldsIncludingNestedComponentJson() {
    String component = "{\"text\":\"Hallo\",\"color\":\"gold\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"/msg Notch \"}}";
    ChatSearchEntry original = new ChatSearchEntry(
        1_700_000_000_000L,
        ChatSearchTab.SECOND,
        "cb2",
        component,
        "Hallo"
    );

    ChatSearchEntry decoded = ChatSearchLineCodec.decode(ChatSearchLineCodec.encode(original));

    assertNotNull(decoded);
    assertEquals(original.timestamp(), decoded.timestamp());
    assertEquals(ChatSearchTab.SECOND, decoded.tab());
    assertEquals("cb2", decoded.subServer());
    assertEquals("Hallo", decoded.plainText());
    assertTrue(decoded.componentJson().contains("\"text\":\"Hallo\""));
    assertTrue(decoded.componentJson().contains("suggest_command"));
  }

  @Test
  void rejectsBlankAndBrokenLines() {
    assertNull(ChatSearchLineCodec.decode(""));
    assertNull(ChatSearchLineCodec.decode("{not json"));
    assertNull(ChatSearchLineCodec.decode("{\"tab\":\"MAIN\"}"));
  }
}
