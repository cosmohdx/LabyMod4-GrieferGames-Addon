package de.cosmohdx.griefergames.feature.chat;

import java.util.regex.Pattern;

/**
 * Matches a chat name to the tab list without asking who a nick really is.
 * The displayed name wins. The profile name is only a fallback, and a leading {@code ~} may be
 * dropped because the tab entry stores the nick the client already shows.
 */
public final class TabListNames {

  private TabListNames() {
  }

  public static <T> T find(Iterable<T> entries, String senderName, NameView<T> names) {
    if (entries == null || senderName == null || senderName.isBlank() || names == null) {
      return null;
    }
    T profileFallback = null;
    for (T entry : entries) {
      if (entry == null) {
        continue;
      }
      if (displayMatches(names.displayPlain(entry), senderName)) {
        return entry;
      }
      if (profileFallback == null && profileMatches(names.profileName(entry), senderName)) {
        profileFallback = entry;
      }
    }
    return profileFallback;
  }

  static boolean displayMatches(String displayPlain, String senderName) {
    if (displayPlain == null || displayPlain.isBlank() || senderName == null) {
      return false;
    }
    if (displayPlain.equals(senderName)) {
      return true;
    }
    return Pattern.compile("\u2503\\s+" + Pattern.quote(senderName) + "(?:\\s|$)")
        .matcher(displayPlain)
        .find();
  }

  static boolean profileMatches(String profileName, String senderName) {
    if (profileName == null || profileName.isBlank() || senderName == null) {
      return false;
    }
    if (profileName.equalsIgnoreCase(senderName)) {
      return true;
    }
    return senderName.startsWith("~") && profileName.equalsIgnoreCase(senderName.substring(1));
  }

  public interface NameView<T> {
    String profileName(T entry);

    String displayPlain(T entry);
  }
}
