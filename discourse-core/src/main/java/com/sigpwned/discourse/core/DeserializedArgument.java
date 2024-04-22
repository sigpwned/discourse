package com.sigpwned.discourse.core;

import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.coordinate.Coordinate;
import com.sigpwned.discourse.core.parameter.ConfigurationParameter;

public record DeserializedArgument(Coordinate coordinate, ConfigurationParameter parameter,
    String argumentText, Object argumentValue) {

  public DeserializedArgument {
    coordinate = requireNonNull(coordinate);
    parameter = requireNonNull(parameter);
  }
}