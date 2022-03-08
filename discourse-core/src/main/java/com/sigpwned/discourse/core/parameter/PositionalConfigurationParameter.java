package com.sigpwned.discourse.core.parameter;

import static java.util.Collections.singleton;
import static java.util.Collections.unmodifiableSet;
import java.util.Set;
import com.sigpwned.discourse.core.ConfigurationClass;
import com.sigpwned.discourse.core.ConfigurationParameter;
import com.sigpwned.discourse.core.Coordinate;
import com.sigpwned.discourse.core.ValueDeserializer;
import com.sigpwned.discourse.core.ValueSink;
import com.sigpwned.discourse.core.coordinate.PositionCoordinate;

public class PositionalConfigurationParameter extends ConfigurationParameter {
  private final PositionCoordinate position;
  
  public PositionalConfigurationParameter(ConfigurationClass configurationClass, String name,
      String description, boolean required, ValueDeserializer<?> deserializer, ValueSink sink,
      PositionCoordinate position) {
    super(configurationClass, Type.POSITIONAL, name, description, required, deserializer, sink);
    this.position = position;
  }

  /**
   * @return the position
   */
  public PositionCoordinate getPosition() {
    return position;
  }

  @Override
  public boolean isValued() {
    return false;
  }

  @Override
  public Set<Coordinate> getCoordinates() {
    return unmodifiableSet(singleton(getPosition()));
  }
}
