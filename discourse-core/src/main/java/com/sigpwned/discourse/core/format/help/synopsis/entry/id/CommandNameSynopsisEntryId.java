package com.sigpwned.discourse.core.format.help.synopsis.entry.id;

/**
 * Represents the syntax for the command name in a synopsis.
 */
public class CommandNameSynopsisEntryId extends SynopsisEntryId {
  public static final CommandNameSynopsisEntryId INSTANCE = new CommandNameSynopsisEntryId();

  private CommandNameSynopsisEntryId() {}

  @Override
  public int hashCode() {
    return 0;
  }

  @Override
  public boolean equals(Object obj) {
    return obj == this;
  }
}
