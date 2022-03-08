package com.sigpwned.discourse.core.exception.configuration;

import static java.lang.String.format;
import com.sigpwned.discourse.core.ConfigurationException;
import com.sigpwned.discourse.core.Coordinate;

public class DuplicateCoordinateConfigurationException extends ConfigurationException {
  private final Coordinate coordinate;

  public DuplicateCoordinateConfigurationException(Coordinate coordinate) {
    super(format("The coordinate %s appears on multiple parameters", coordinate));
    this.coordinate = coordinate;
  }

  /**
   * @return the coordinate
   */
  public Coordinate getCoordinate() {
    return coordinate;
  }
}
