package com.sigpwned.discourse.core.exception.user;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import com.sigpwned.discourse.core.args.Coordinate;
import com.sigpwned.discourse.core.exception.UserDiscourseException;

@SuppressWarnings("serial")
public class InvalidArgumentUserDiscourseException extends UserDiscourseException {
  private final String propertyName;
  private final Coordinate coordinate;
  private final String value;

  public InvalidArgumentUserDiscourseException(String propertyName, Coordinate coordinate,
      String value) {
    this(propertyName, coordinate, value, null);
  }

  public InvalidArgumentUserDiscourseException(String propertyName, Coordinate coordinate,
      String value, Throwable cause) {
    super(format("Invalid argument %s for property %s at %s", value, propertyName, coordinate),
        cause);
    this.propertyName = requireNonNull(propertyName);
    this.coordinate = requireNonNull(coordinate);
    this.value = requireNonNull(value);
  }

  /**
   * @return the propertyName
   */
  public String getPropertyName() {
    return propertyName;
  }

  /**
   * @return the coordinate
   */
  public Coordinate getCoordinate() {
    return coordinate;
  }

  /**
   * @return the value
   */
  public String getValue() {
    return value;
  }

  @Override
  protected Object[] getLocalizedMessageArguments() {
    return new Object[] {getPropertyName(), getCoordinate(), getValue()};
  }
}
