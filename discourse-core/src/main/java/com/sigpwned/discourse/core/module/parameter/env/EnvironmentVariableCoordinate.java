package com.sigpwned.discourse.core.module.parameter.env;

import static java.util.Objects.requireNonNull;
import java.util.Objects;
import com.sigpwned.discourse.core.args.Coordinate;

public class EnvironmentVariableCoordinate extends Coordinate {
  private final String variableName;

  public EnvironmentVariableCoordinate(String variableName) {
    this.variableName = requireNonNull(variableName);
  }

  public String getVariableName() {
    return variableName;
  }

  @Override
  public int hashCode() {
    return Objects.hash(variableName);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    EnvironmentVariableCoordinate other = (EnvironmentVariableCoordinate) obj;
    return Objects.equals(variableName, other.variableName);
  }

  @Override
  public String toString() {
    return "EnvironmentVariableCoordinate[" + variableName + "]";
  }
}
