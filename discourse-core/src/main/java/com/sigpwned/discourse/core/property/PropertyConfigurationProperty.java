package com.sigpwned.discourse.core.property;

import java.util.Objects;
import com.sigpwned.discourse.core.ConfigurationClass;
import com.sigpwned.discourse.core.ConfigurationProperty;
import com.sigpwned.discourse.core.ValueStorer;
import com.sigpwned.discourse.core.exception.configuration.InvalidPropertyNameConfigurationException;
import com.sigpwned.espresso.BeanProperty;

public class PropertyConfigurationProperty extends ConfigurationProperty {
  private final String propertyName;

  public PropertyConfigurationProperty(ConfigurationClass configurationClass, BeanProperty property,
      ValueStorer storer, String description, String propertyName, boolean required) {
    super(configurationClass, property, storer, description, required);
    if (propertyName == null)
      throw new NullPointerException();
    if (propertyName.isBlank())
      throw new InvalidPropertyNameConfigurationException(propertyName);
    this.propertyName = propertyName;
  }

  /**
   * @return the propertyName
   */
  public String getPropertyName() {
    return propertyName;
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
    return "PropertyConfigurationProperty [variableName=" + propertyName + "]";
  }
}
