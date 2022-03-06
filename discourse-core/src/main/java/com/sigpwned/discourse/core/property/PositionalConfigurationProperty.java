package com.sigpwned.discourse.core.property;

import static java.util.Collections.singleton;
import static java.util.Collections.unmodifiableSet;
import java.util.Objects;
import java.util.Set;
import com.sigpwned.discourse.core.ConfigurationClass;
import com.sigpwned.discourse.core.ConfigurationProperty;
import com.sigpwned.discourse.core.Coordinate;
import com.sigpwned.discourse.core.ValueStorer;
import com.sigpwned.discourse.core.coordinate.PositionCoordinate;
import com.sigpwned.espresso.BeanProperty;

public class PositionalConfigurationProperty extends ConfigurationProperty {
  private final PositionCoordinate position;

  public PositionalConfigurationProperty(ConfigurationClass configurationClass,
      BeanProperty property, ValueStorer storer, String description, PositionCoordinate position,
      boolean required) {
    super(configurationClass, property, storer, description, required);
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

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + Objects.hash(position);
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
    PositionalConfigurationProperty other = (PositionalConfigurationProperty) obj;
    return position == other.position;
  }

  @Override
  public String toString() {
    return "PositionalConfigurationProperty [position=" + position + "]";
  }
}
