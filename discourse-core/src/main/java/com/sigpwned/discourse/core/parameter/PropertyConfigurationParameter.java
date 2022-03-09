package com.sigpwned.discourse.core.parameter;

import static java.util.Collections.singleton;
import static java.util.Collections.unmodifiableSet;
import java.util.Objects;
import java.util.Set;
import com.sigpwned.discourse.core.ConfigurationParameter;
import com.sigpwned.discourse.core.Coordinate;
import com.sigpwned.discourse.core.ValueDeserializer;
import com.sigpwned.discourse.core.ValueSink;
import com.sigpwned.discourse.core.coordinate.name.PropertyNameCoordinate;
import com.sigpwned.discourse.core.util.Generated;

public class PropertyConfigurationParameter extends ConfigurationParameter {
  private final PropertyNameCoordinate propertyName;

  public PropertyConfigurationParameter(String name, String description, boolean required,
      ValueDeserializer<?> deserializer, ValueSink sink, PropertyNameCoordinate propertyName) {
    super(Type.PROPERTY, name, description, required, deserializer, sink);
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

  @Override
  @Generated
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + Objects.hash(propertyName);
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
    PropertyConfigurationParameter other = (PropertyConfigurationParameter) obj;
    return Objects.equals(propertyName, other.propertyName);
  }
}
