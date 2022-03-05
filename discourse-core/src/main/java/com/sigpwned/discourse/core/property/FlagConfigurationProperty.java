package com.sigpwned.discourse.core.property;

import java.util.Objects;
import com.sigpwned.discourse.core.ConfigurationClass;
import com.sigpwned.discourse.core.ConfigurationProperty;
import com.sigpwned.discourse.core.ValueStorer;
import com.sigpwned.discourse.core.exception.configuration.InvalidLongNameConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.InvalidShortNameConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.NoNameConfigurationException;
import com.sigpwned.discourse.core.util.Parameters;
import com.sigpwned.espresso.BeanProperty;

public class FlagConfigurationProperty extends ConfigurationProperty {
  private final String shortName;
  private final String longName;

  public FlagConfigurationProperty(ConfigurationClass configurationClass, BeanProperty property,
      ValueStorer storer, String description, String shortName, String longName) {
    super(configurationClass, property, storer, description, false);
    if (shortName == null && longName == null)
      throw new NoNameConfigurationException(property.getName());
    if (shortName != null && !Parameters.SHORT_NAME_PATTERN.matcher(shortName).matches())
      throw new InvalidShortNameConfigurationException(shortName);
    if (longName != null && !Parameters.LONG_NAME_PATTERN.matcher(longName).matches())
      throw new InvalidLongNameConfigurationException(longName);
    this.shortName = shortName;
    this.longName = longName;
  }

  /**
   * @return the shortName
   */
  public String getShortName() {
    return shortName;
  }

  /**
   * @return the longName
   */
  public String getLongName() {
    return longName;
  }

  @Override
  public boolean isValued() {
    return false;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + Objects.hash(longName, shortName);
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
    FlagConfigurationProperty other = (FlagConfigurationProperty) obj;
    return Objects.equals(longName, other.longName) && Objects.equals(shortName, other.shortName);
  }

  @Override
  public String toString() {
    return "FlagConfigurationProperty [shortName=" + shortName + ", longName=" + longName + "]";
  }
}
