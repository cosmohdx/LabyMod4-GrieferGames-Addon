package de.cosmohdx.griefergames.feature.chat;

/**
 * Told when a user-supplied second-chat expression cannot be compiled.
 */
@FunctionalInterface
interface InvalidPatternReporter {

  void report(String categoryId, String pattern, String error);
}
