package com.sigpwned.discourse.core.property;

import static java.util.Collections.unmodifiableSet;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import com.sigpwned.discourse.core.ConfigurationClass;
import com.sigpwned.discourse.core.ConfigurationProperty;
import com.sigpwned.discourse.core.Coordinate;
import com.sigpwned.discourse.core.ValueDeserializer;
import com.sigpwned.discourse.core.ValueSink;
import com.sigpwned.discourse.core.coordinate.NameCoordinate;
import com.sigpwned.discourse.core.coordinate.name.switches.LongSwitchNameCoordinate;
import com.sigpwned.discourse.core.coordinate.name.switches.ShortSwitchNameCoordinate;

public class FlagConfigurationProperty extends ConfigurationProperty {
  private final ShortSwitchNameCoordinate shortName;
  private final LongSwitchNameCoordinate longName;

  public FlagConfigurationProperty(ConfigurationClass configurationClass, String name,
      String description, ValueDeserializer<?> deserializer, ValueSink sink,
      ShortSwitchNameCoordinate shortName, LongSwitchNameCoordinate longName) {
    super(configurationClass, name, description, false, deserializer, sink);
    if (shortName == null && longName == null)
      throw new IllegalArgumentException("no names");
    this.shortName = shortName;
    this.longName = longName;
  }

  /**
   * @return the shortName
   */
  public ShortSwitchNameCoordinate getShortName() {
    return shortName;
  }

  /**
   * @return the longName
   */
  public LongSwitchNameCoordinate getLongName() {
    return longName;
  }

  @Override
  public boolean isValued() {
    return false;
  }

  @Override
  public Set<Coordinate> getCoordinates() {
    Set<NameCoordinate> result = new HashSet<>(2);
    if (getShortName() != null)
      result.add(getShortName());
    if (getLongName() != null)
      result.add(getLongName());
    return unmodifiableSet(result);
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
