package com.sigpwned.discourse.core.property;

import java.util.Objects;
import com.sigpwned.discourse.core.ConfigurationClass;
import com.sigpwned.discourse.core.ConfigurationProperty;
import com.sigpwned.discourse.core.ValueStorer;
import com.sigpwned.discourse.core.exception.configuration.InvalidVariableNameConfigurationException;
import com.sigpwned.espresso.BeanProperty;

public class EnvironmentConfigurationProperty extends ConfigurationProperty {
  private final String variableName;

  public EnvironmentConfigurationProperty(ConfigurationClass configurationClass,
      BeanProperty property, ValueStorer storer, String description, String variableName,
      boolean required) {
    super(configurationClass, property, storer, description, required);
    if (variableName == null)
      throw new NullPointerException();
    if (variableName.isBlank())
      throw new InvalidVariableNameConfigurationException(variableName);
    this.variableName = variableName;
  }

  /**
   * @return the variableName
   */
  public String getVariableName() {
    return variableName;
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
