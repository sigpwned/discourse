package com.sigpwned.discourse.core.format.help.synopsis.entry.id;

/**
 * Represents a discriminator in a synopsis. The given depth is the number of levels of nesting in
 * the discriminator, i.e., the distance from the logical command root, with 0 being the root
 * itself, 1 being the first level of nesting, and so on.
 */
public class DiscriminatorSynopsisEntryId extends SynopsisEntryId {
  public static DiscriminatorSynopsisEntryId of(int depth) {
    return new DiscriminatorSynopsisEntryId(depth);
  }

  private final int depth;

  public DiscriminatorSynopsisEntryId(int depth) {
    this.depth = depth;
    if (depth < 0)
      throw new IllegalArgumentException("depth must not be negative");
  }

  @Override
  public int hashCode() {
    return Integer.hashCode(depth);
  }

  @Override
  public boolean equals(Object other) {
    if (this == other)
      return true;
    if (other == null)
      return false;
    if (getClass() != other.getClass())
      return false;
    DiscriminatorSynopsisEntryId that = (DiscriminatorSynopsisEntryId) other;
    return this.depth == that.depth;
  }
}
