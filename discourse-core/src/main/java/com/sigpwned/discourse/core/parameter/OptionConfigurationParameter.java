package com.sigpwned.discourse.core.parameter;

import static java.util.Collections.unmodifiableSet;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import com.sigpwned.discourse.core.ConfigurationParameter;
import com.sigpwned.discourse.core.Coordinate;
import com.sigpwned.discourse.core.ValueDeserializer;
import com.sigpwned.discourse.core.ValueSink;
import com.sigpwned.discourse.core.coordinate.NameCoordinate;
import com.sigpwned.discourse.core.coordinate.name.switches.LongSwitchNameCoordinate;
import com.sigpwned.discourse.core.coordinate.name.switches.ShortSwitchNameCoordinate;
import com.sigpwned.discourse.core.util.Generated;

public class OptionConfigurationParameter extends ConfigurationParameter {
  private final ShortSwitchNameCoordinate shortName;
  private final LongSwitchNameCoordinate longName;

  public OptionConfigurationParameter(String name, String description, boolean required,
      ValueDeserializer<?> deserializer, ValueSink sink, ShortSwitchNameCoordinate shortName,
      LongSwitchNameCoordinate longName) {
    super(Type.OPTION, name, description, required, deserializer, sink);
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
    return true;
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
  @Generated
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + Objects.hash(longName, shortName);
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
    OptionConfigurationParameter other = (OptionConfigurationParameter) obj;
    return Objects.equals(longName, other.longName) && Objects.equals(shortName, other.shortName);
  }
}
