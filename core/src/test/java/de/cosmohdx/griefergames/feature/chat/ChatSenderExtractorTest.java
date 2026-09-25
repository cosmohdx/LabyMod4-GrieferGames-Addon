package de.cosmohdx.griefergames.feature.chat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.cosmohdx.griefergames.feature.chat.ChatSender.Kind;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class ChatSenderExtractorTest {

  @Test
  void publicCitybuildAndGlobalLinesUseTheDisplayedName() {
    assertSender("Supreme+ ┃ Notch » Hallo", "Notch", Kind.PUBLIC);
    assertSender("Co-Owner ┃ ~Notch » Handel", "~Notch", Kind.PUBLIC);
    assertSender("Supreme+ ┃ !Notch » Hallo", "!Notch", Kind.PUBLIC);
    assertSender("[G] Supreme+ ┃ Notch » global", "Notch", Kind.PUBLIC);
    assertSender("[Clan] Co-Owner ┃ !Notch » clan", "!Notch", Kind.PUBLIC);
  }

  @Test
  void privateMessagesUseThePartner() {
    assertSender("[Supreme+ ┃ ~Notch -> mir] Hallo", "~Notch", Kind.PRIVATE);
    assertSender("[Supreme+ ┃ !Notch -> me] Hello", "!Notch", Kind.PRIVATE);
    assertSender("§6[Co-Owner ┃ Notch -> mir] Hallo", "Notch", Kind.PRIVATE);
    assertSender("[mir -> Supreme+ ┃ Notch] Hallo zurück", "Notch", Kind.PRIVATE);
    assertSender("[me -> Co-Owner ┃ !Notch] Hello", "!Notch", Kind.PRIVATE);
  }

  @Test
  void plotChatUsesTheSpeaker() {
    assertSender("[Plot-Chat] Notch: Hallo auf dem Plot", "Notch", Kind.PLOT);
    assertSender("[Plot-Chat] ~Notch: hi", "~Notch", Kind.PLOT);
    assertSender("[Plot-Chat] !Notch: hi", "!Notch", Kind.PLOT);
    assertSender("[Plot-Chat] Supreme+ ┃ Notch: hi", "Notch", Kind.PLOT);
  }

  @Test
  void paymentsStatusAndSystemLinesHaveNoSender() {
    assertEmpty("Supreme+ ┃ Notch hat dir $1,234.50 gegeben.");
    assertEmpty("Du hast Co-Owner ┃ ~Notch $50 gegeben.");
    assertEmpty("$1,250.50 wurde zu deinem Konto hinzugefügt.");
    assertEmpty("Kontostand: $1,000");
    assertEmpty("[Status] Supreme+ ┃ Notch: Im Handel");
    assertEmpty("[GrieferGames] Willkommen");
    assertEmpty("   ");
    assertEmpty(null);
  }

  private static void assertSender(String line, String name, Kind kind) {
    Optional<ChatSender> sender = ChatSenderExtractor.sender(line);
    assertTrue(sender.isPresent(), line);
    assertEquals(name, sender.get().name());
    assertEquals(kind, sender.get().kind());
  }

  private static void assertEmpty(String line) {
    assertTrue(ChatSenderExtractor.sender(line).isEmpty(), String.valueOf(line));
  }
}
