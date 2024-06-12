package com.sigpwned.discourse.core.format.help.synopsis.entry;

public class DiscriminatorSynopsisEntry extends SynopsisEntry {
  private final int index;

  public DiscriminatorSynopsisEntry(int index) {
    this.index = index;
    if (index < 0)
      throw new IllegalArgumentException("index must not be negative");
  }

  /**
   * @return the index
   */
  public int getIndex() {
    return index;
  }
}
