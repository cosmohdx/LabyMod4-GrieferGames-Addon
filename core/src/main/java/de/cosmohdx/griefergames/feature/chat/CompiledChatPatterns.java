package de.cosmohdx.griefergames.feature.chat;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Compiles one category's patterns once and again when the source list changes.
 *
 * <p>An invalid expression is skipped. The text stays in the config, a warning is logged, and
 * {@link #hint()} keeps the syntax error so it can be shown to the user.
 */
final class CompiledChatPatterns {

  private static final Logger LOG = Logger.getLogger("de.cosmohdx.griefergames");

  private InvalidPatternReporter reporter = (categoryId, pattern, error) -> LOG.log(
      Level.WARNING,
      "[GrieferGames-Addon] Second chat category {0} skipped invalid pattern \"{1}\": {2}",
      new Object[] {categoryId, pattern, error}
  );
  private String fingerprint = "";
  private List<Pattern> compiled = List.of();
  private List<String> invalid = List.of();
  private String hint = "";

  boolean matches(String categoryId, String text, List<String> sources) {
    this.ensure(categoryId, sources);
    if (text == null) {
      return false;
    }
    for (Pattern pattern : this.compiled) {
      if (pattern.matcher(text).find()) {
        return true;
      }
    }
    return false;
  }

  String hint(String categoryId, List<String> sources) {
    this.ensure(categoryId, sources);
    return this.hint;
  }

  void reportTo(InvalidPatternReporter reporter) {
    this.reporter = reporter;
  }

  private void ensure(String categoryId, List<String> sources) {
    String next = fingerprint(sources);
    if (next.equals(this.fingerprint)) {
      return;
    }
    List<Pattern> compiled = new ArrayList<>();
    List<String> invalid = new ArrayList<>();
    StringBuilder hint = new StringBuilder();
    for (String source : sources) {
      if (source == null || source.isBlank()) {
        continue;
      }
      try {
        compiled.add(Pattern.compile(source));
      } catch (PatternSyntaxException exception) {
        invalid.add(source);
        if (!hint.isEmpty()) {
          hint.append('\n');
        }
        hint.append(source).append(": ").append(exception.getDescription());
        if (!this.invalid.contains(source)) {
          this.reporter.report(
              categoryId == null ? "unknown" : categoryId,
              source,
              exception.getDescription()
          );
        }
      }
    }
    this.compiled = List.copyOf(compiled);
    this.invalid = List.copyOf(invalid);
    this.hint = hint.toString();
    this.fingerprint = next;
  }

  private static String fingerprint(List<String> sources) {
    StringBuilder fingerprint = new StringBuilder();
    for (String source : sources) {
      fingerprint.append(source == null ? "" : source).append('\0');
    }
    return fingerprint.toString();
  }
}
