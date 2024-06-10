package com.sigpwned.discourse.core.format.help.synopsis.entry.id;

/**
 * <p>
 * Represents the syntax for an "options" entry in a synopsis, like:
 * </p>
 * 
 * <pre>
 *     command [options] arg1 arg2 ...
 * </pre>
 */
public class OptionsPlaceholderSynopsisEntryId extends SynopsisEntryId {
  public static final OptionsPlaceholderSynopsisEntryId INSTANCE = new OptionsPlaceholderSynopsisEntryId();

  private OptionsPlaceholderSynopsisEntryId() {}

  @Override
  public int hashCode() {
    return 0;
  }

  @Override
  public boolean equals(Object obj) {
    return obj == this;
  }
}
