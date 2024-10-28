package com.sigpwned.discourse.core.format.help.model.synopsis;

import static java.util.Objects.requireNonNull;
import com.sigpwned.discourse.core.args.coordinate.PositionalCoordinate;

public class PositionalArgumentCommandSynopsisEntry extends CommandSynopsisEntry {
  private final PositionalCoordinate coordinate;

  public PositionalArgumentCommandSynopsisEntry(PositionalCoordinate coordinate) {
    this.coordinate = requireNonNull(coordinate);
  }

  /**
   * @return the coordinate
   */
  public PositionalCoordinate getCoordinate() {
    return coordinate;
  }
}
