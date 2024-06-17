package com.sigpwned.discourse.core.format.help.model.synopsis;

public class DiscriminatorCommandSynopsisEntry extends CommandSynopsisEntry {
  private final int index;

  public DiscriminatorCommandSynopsisEntry(int index) {
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
