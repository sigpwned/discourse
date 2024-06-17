package com.sigpwned.discourse.core.format.help.model.synopsis;

/**
 * Represents a literal entry in a synopsis. This is used to represent a literal string in a
 * synopsis, for example "java" in "java [options] classname [args]".
 */
public class LiteralCommandSynopsisEntry extends CommandSynopsisEntry {
  private final String text;

  public LiteralCommandSynopsisEntry(String text) {
    this.text = text;
  }

  public String getText() {
    return this.text;
  }
}
