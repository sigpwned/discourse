package com.sigpwned.discourse.core.format.help.synopsis.entry.id;

import static java.util.Objects.requireNonNull;
import java.util.Objects;
import com.sigpwned.discourse.core.args.Coordinate;

/**
 * Represents the syntax for a single {@link Coordinate} in a synopsis.
 */
public class CoordinateSynopsisEntryId extends SynopsisEntryId {
  public static final CoordinateSynopsisEntryId of(Coordinate coordinate) {
    return new CoordinateSynopsisEntryId(coordinate);
  }

  private final Coordinate coordinate;

  public CoordinateSynopsisEntryId(Coordinate coordinate) {
    this.coordinate = requireNonNull(coordinate);
  }

  public Coordinate getCoordinate() {
    return this.coordinate;
  }

  @Override
  public int hashCode() {
    return Objects.hash(coordinate);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    CoordinateSynopsisEntryId other = (CoordinateSynopsisEntryId) obj;
    return Objects.equals(coordinate, other.coordinate);
  }

  @Override
  public String toString() {
    return coordinate.toString();
  }
}
