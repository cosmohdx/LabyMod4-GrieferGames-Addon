package de.cosmohdx.griefergames.feature.chat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;
import org.junit.jupiter.api.Test;

class TabListNamesTest {

  @Test
  void displayedNameWinsOverADifferentProfile() {
    Entry nick = new Entry("RealName", "Supreme+ ┃ ~Notch");
    Entry real = new Entry("Notch", "Supreme+ ┃ Notch");

    assertEquals(nick, TabListNames.find(List.of(real, nick), "~Notch", Entry.VIEW));
    assertEquals(real, TabListNames.find(List.of(real, nick), "Notch", Entry.VIEW));
  }

  @Test
  void profileNameIsTheFallbackForANickMarker() {
    Entry nick = new Entry("Notch", "Spieler");

    assertEquals(nick, TabListNames.find(List.of(nick), "~Notch", Entry.VIEW));
    assertNull(TabListNames.find(List.of(nick), "!Bedrock", Entry.VIEW));
    assertNull(TabListNames.find(List.of(), "Notch", Entry.VIEW));
  }

  private record Entry(String profile, String display) {
    private static final TabListNames.NameView<Entry> VIEW = new TabListNames.NameView<>() {
      @Override
      public String profileName(Entry entry) {
        return entry.profile;
      }

      @Override
      public String displayPlain(Entry entry) {
        return entry.display;
      }
    };
  }
}
