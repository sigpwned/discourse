package com.sigpwned.discourse.core.parameter;

import static java.util.Collections.singleton;
import static java.util.Collections.unmodifiableSet;
import java.util.Set;
import com.sigpwned.discourse.core.ConfigurationClass;
import com.sigpwned.discourse.core.ConfigurationParameter;
import com.sigpwned.discourse.core.Coordinate;
import com.sigpwned.discourse.core.ValueDeserializer;
import com.sigpwned.discourse.core.ValueSink;
import com.sigpwned.discourse.core.coordinate.name.VariableNameCoordinate;

public class EnvironmentConfigurationParameter extends ConfigurationParameter {
  private final VariableNameCoordinate variableName;

  public EnvironmentConfigurationParameter(ConfigurationClass configurationClass, String name,
      String description, boolean required, ValueDeserializer<?> deserializer, ValueSink sink,
      VariableNameCoordinate variableName) {
    super(configurationClass, Type.ENVIRONMENT, name, description, required, deserializer, sink);
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
}
