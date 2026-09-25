package de.cosmohdx.griefergames.feature.chat;

import java.util.List;
import java.util.function.Predicate;

/**
 * Chooses an existing second-chat tab or decides that a new one has to be created.
 *
 * <p>The display name is not an identity. LabyMod stores each tab under a UUID, reloads those
 * objects whenever the advanced chat config is rebuilt, and falls back to the current server name
 * when the stored name is blank. Matching that fallback creates a new tab on every join.
 */
final class SecondChatTabSelector {

  enum Action {
    REUSE,
    CREATE,
    NONE
  }

  record Candidate(String id, String storedName, boolean marked, boolean legacyFilter) {

  }

  record Decision(Action action, int index) {

    static Decision reuse(int index) {
      return new Decision(Action.REUSE, index);
    }

    static Decision create() {
      return new Decision(Action.CREATE, -1);
    }

    static Decision none() {
      return new Decision(Action.NONE, -1);
    }
  }

  private SecondChatTabSelector() {
  }

  static Decision select(
      List<Candidate> tabs,
      String storedId,
      String configuredName,
      String previousName,
      boolean create
  ) {
    String id = normalize(storedId);
    if (id != null) {
      for (int index = 0; index < tabs.size(); index++) {
        if (id.equalsIgnoreCase(normalize(tabs.get(index).id()))) {
          return Decision.reuse(index);
        }
      }
      return create ? Decision.create() : Decision.none();
    }

    int marked = first(tabs, Candidate::marked);
    if (marked >= 0) {
      return Decision.reuse(marked);
    }

    int legacy = first(tabs, Candidate::legacyFilter);
    if (legacy >= 0) {
      return Decision.reuse(legacy);
    }

    int byName = byName(tabs, configuredName);
    if (byName >= 0) {
      return Decision.reuse(byName);
    }

    int byPreviousName = byName(tabs, previousName);
    if (byPreviousName >= 0) {
      return Decision.reuse(byPreviousName);
    }

    return create ? Decision.create() : Decision.none();
  }

  private static int first(List<Candidate> tabs, Predicate<Candidate> predicate) {
    for (int index = 0; index < tabs.size(); index++) {
      if (predicate.test(tabs.get(index))) {
        return index;
      }
    }
    return -1;
  }

  private static int byName(List<Candidate> tabs, String name) {
    String wanted = normalize(name);
    if (wanted == null) {
      return -1;
    }
    for (int index = 0; index < tabs.size(); index++) {
      String storedName = normalize(tabs.get(index).storedName());
      if (wanted.equalsIgnoreCase(storedName)) {
        return index;
      }
    }
    return -1;
  }

  private static String normalize(String value) {
    if (value == null) {
      return null;
    }
    String trimmed = value.trim();
    return trimmed.isEmpty() ? null : trimmed;
  }
}
