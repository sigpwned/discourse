package com.sigpwned.discourse.core.parameter;

import static java.util.Collections.singleton;
import static java.util.Collections.unmodifiableSet;
import java.util.Set;
import com.sigpwned.discourse.core.ConfigurationClass;
import com.sigpwned.discourse.core.ConfigurationParameter;
import com.sigpwned.discourse.core.Coordinate;
import com.sigpwned.discourse.core.ValueDeserializer;
import com.sigpwned.discourse.core.ValueSink;
import com.sigpwned.discourse.core.coordinate.name.PropertyNameCoordinate;

public class PropertyConfigurationParameter extends ConfigurationParameter {
  private final PropertyNameCoordinate propertyName;
  
  public PropertyConfigurationParameter(ConfigurationClass configurationClass, String name,
      String description, boolean required, ValueDeserializer<?> deserializer, ValueSink sink,
      PropertyNameCoordinate propertyName) {
    super(configurationClass, Type.PROPERTY, name, description, required, deserializer, sink);
    if (propertyName == null)
      throw new NullPointerException();
    this.propertyName = propertyName;
  }

  /**
   * @return the propertyName
   */
  public PropertyNameCoordinate getPropertyName() {
    return propertyName;
  }
  
  @Override
  public Set<Coordinate> getCoordinates() {
    return unmodifiableSet(singleton(getPropertyName()));
  }
  
  @Override
  public boolean isValued() {
    return false;
  }
}
