package com.sigpwned.discourse.core.property;

import static java.util.Collections.singleton;
import static java.util.Collections.unmodifiableSet;
import java.util.Objects;
import java.util.Set;
import com.sigpwned.discourse.core.ConfigurationClass;
import com.sigpwned.discourse.core.ConfigurationProperty;
import com.sigpwned.discourse.core.Coordinate;
import com.sigpwned.discourse.core.ValueStorer;
import com.sigpwned.discourse.core.coordinate.name.PropertyNameCoordinate;
import com.sigpwned.espresso.BeanProperty;

public class PropertyConfigurationProperty extends ConfigurationProperty {
  private final PropertyNameCoordinate propertyName;

  public PropertyConfigurationProperty(ConfigurationClass configurationClass, BeanProperty property,
      ValueStorer storer, String description, PropertyNameCoordinate propertyName, boolean required) {
    super(configurationClass, property, storer, description, required);
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
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + Objects.hash(propertyName);
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
    PropertyConfigurationProperty other = (PropertyConfigurationProperty) obj;
    return Objects.equals(propertyName, other.propertyName);
  }

  @Override
  public String toString() {
    return "PropertyConfigurationProperty [propertyName=" + propertyName + "]";
  }
}
