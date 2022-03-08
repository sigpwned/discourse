package com.sigpwned.discourse.core.parameter;

import static java.util.Collections.unmodifiableSet;
import java.util.HashSet;
import java.util.Set;
import com.sigpwned.discourse.core.ConfigurationClass;
import com.sigpwned.discourse.core.ConfigurationParameter;
import com.sigpwned.discourse.core.Coordinate;
import com.sigpwned.discourse.core.ValueDeserializer;
import com.sigpwned.discourse.core.ValueSink;
import com.sigpwned.discourse.core.coordinate.NameCoordinate;
import com.sigpwned.discourse.core.coordinate.name.switches.LongSwitchNameCoordinate;
import com.sigpwned.discourse.core.coordinate.name.switches.ShortSwitchNameCoordinate;

public class FlagConfigurationParameter extends ConfigurationParameter {
  private final ShortSwitchNameCoordinate shortName;
  private final LongSwitchNameCoordinate longName;

  public FlagConfigurationParameter(ConfigurationClass configurationClass, String name,
      String description, ValueDeserializer<?> deserializer, ValueSink sink,
      ShortSwitchNameCoordinate shortName, LongSwitchNameCoordinate longName) {
    super(configurationClass, Type.FLAG, name, description, false, deserializer, sink);
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
}
