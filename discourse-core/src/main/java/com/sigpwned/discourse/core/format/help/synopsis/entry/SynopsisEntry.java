package com.sigpwned.discourse.core.format.help.synopsis.entry;

import static java.util.Objects.requireNonNull;
import com.sigpwned.discourse.core.format.help.synopsis.entry.id.SynopsisEntryId;

/**
 * A single entry in a synopsis.
 */
public abstract class SynopsisEntry {
  private final SynopsisEntryId id;

  public SynopsisEntry(SynopsisEntryId id) {
    this.id = requireNonNull(id);
  }

  public SynopsisEntryId getId() {
    return this.id;
  }

  public abstract String getText();
}
