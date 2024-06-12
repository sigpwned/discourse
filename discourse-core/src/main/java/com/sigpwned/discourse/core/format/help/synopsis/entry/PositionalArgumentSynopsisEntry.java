package com.sigpwned.discourse.core.format.help.synopsis.entry;

import static java.util.Objects.requireNonNull;
import com.sigpwned.discourse.core.args.coordinate.PositionalCoordinate;

public class PositionalArgumentSynopsisEntry extends SynopsisEntry {
  private final PositionalCoordinate coordinate;

  public PositionalArgumentSynopsisEntry(PositionalCoordinate coordinate) {
    this.coordinate = requireNonNull(coordinate);
  }

  /**
   * @return the coordinate
   */
  public PositionalCoordinate getCoordinate() {
    return coordinate;
  }
}
