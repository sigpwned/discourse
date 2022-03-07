package com.sigpwned.discourse.core.property;

import static java.util.Collections.singleton;
import static java.util.Collections.unmodifiableSet;
import java.util.Objects;
import java.util.Set;
import com.sigpwned.discourse.core.ConfigurationClass;
import com.sigpwned.discourse.core.ConfigurationProperty;
import com.sigpwned.discourse.core.Coordinate;
import com.sigpwned.discourse.core.ValueDeserializer;
import com.sigpwned.discourse.core.ValueSink;
import com.sigpwned.discourse.core.coordinate.name.VariableNameCoordinate;

public class EnvironmentConfigurationProperty extends ConfigurationProperty {
  private final VariableNameCoordinate variableName;

  public EnvironmentConfigurationProperty(ConfigurationClass configurationClass, String name,
      String description, boolean required, ValueDeserializer<?> deserializer, ValueSink sink,
      VariableNameCoordinate variableName) {
    super(configurationClass, name, description, required, deserializer, sink);
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
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + Objects.hash(variableName);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    EnvironmentConfigurationProperty other = (EnvironmentConfigurationProperty) obj;
    return Objects.equals(variableName, other.variableName);
  }

  @Override
  public String toString() {
    return "EnvironmentConfigurationProperty [variableName=" + variableName + "]";
  }
}
