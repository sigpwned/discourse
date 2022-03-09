package com.sigpwned.discourse.core.parameter;

import static java.util.Collections.singleton;
import static java.util.Collections.unmodifiableSet;
import java.util.Objects;
import java.util.Set;
import com.sigpwned.discourse.core.ConfigurationParameter;
import com.sigpwned.discourse.core.Coordinate;
import com.sigpwned.discourse.core.ValueDeserializer;
import com.sigpwned.discourse.core.ValueSink;
import com.sigpwned.discourse.core.coordinate.name.VariableNameCoordinate;
import com.sigpwned.discourse.core.util.Generated;

public class EnvironmentConfigurationParameter extends ConfigurationParameter {
  private final VariableNameCoordinate variableName;

  public EnvironmentConfigurationParameter(String name, String description, boolean required,
      ValueDeserializer<?> deserializer, ValueSink sink, VariableNameCoordinate variableName) {
    super(Type.ENVIRONMENT, name, description, required, deserializer, sink);
    if (variableName == null)
      throw new NullPointerException();
    this.variableName = variableName;
  }

  /**
   * @return the variableName
   */
  public VariableNameCoordinate getVariableName() {
    return variableName;
  }

  @Override
  public Set<Coordinate> getCoordinates() {
    return unmodifiableSet(singleton(getVariableName()));
  }

  @Override
  public boolean isValued() {
    return false;
  }

  @Override
  @Generated
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + Objects.hash(variableName);
    return result;
  }

  @Override
  @Generated
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    EnvironmentConfigurationParameter other = (EnvironmentConfigurationParameter) obj;
    return Objects.equals(variableName, other.variableName);
  }
}
