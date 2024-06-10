package com.sigpwned.discourse.core.format.help.synopsis.entry;

import com.sigpwned.discourse.core.format.help.synopsis.entry.id.SynopsisEntryId;

/**
 * Represents a literal entry in a synopsis. This is used to represent a literal string in a
 * synopsis, for example a command name or a discriminator.
 */
public class LiteralSynopsisEntry extends SynopsisEntry {
  private final String text;

  public LiteralSynopsisEntry(SynopsisEntryId id, String text) {
    super(id);
    this.text = text;
  }

  @Override
  public String getText() {
    return this.text;
  }
}
