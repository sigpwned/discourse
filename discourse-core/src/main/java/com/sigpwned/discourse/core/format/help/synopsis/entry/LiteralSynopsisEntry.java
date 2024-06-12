package com.sigpwned.discourse.core.format.help.synopsis.entry;

/**
 * Represents a literal entry in a synopsis. This is used to represent a literal string in a
 * synopsis, for example "java" in "java [options] classname [args]".
 */
public class LiteralSynopsisEntry extends SynopsisEntry {
  private final String text;

  public LiteralSynopsisEntry(String text) {
    this.text = text;
  }

  public String getText() {
    return this.text;
  }
}
