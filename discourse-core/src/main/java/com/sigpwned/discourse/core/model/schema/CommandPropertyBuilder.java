package com.sigpwned.discourse.core.model.schema;

import com.sigpwned.discourse.core.model.coordinate.LongSwitchNameCoordinate;
import com.sigpwned.discourse.core.model.coordinate.PropertyNameCoordinate;
import com.sigpwned.discourse.core.model.coordinate.ShortSwitchNameCoordinate;
import com.sigpwned.discourse.core.model.coordinate.VariableNameCoordinate;
import java.lang.reflect.Type;

public class CommandPropertyBuilder {

  private final String name;
  private final String description;
  private final Type genericType;
  private final boolean required;

  public void flag(ShortSwitchNameCoordinate shortName, LongSwitchNameCoordinate longName) {
    if (shortName == null && longName == null) {
      throw new IllegalArgumentException("at least one of shortName and longName must not be null");
    }

  }

  public void option(ShortSwitchNameCoordinate shortName, LongSwitchNameCoordinate longName) {
    if (shortName == null && longName == null) {
      throw new IllegalArgumentException("at least one of shortName and longName must not be null");
    }

  }

  public void positional(int position, boolean collection) {
    if (position < 0) {
      throw new IllegalArgumentException("position must not be negative");
    }
  }

  public void variable(VariableNameCoordinate variable) {
    if (variable == null) {
      throw new IllegalArgumentException("variable must not be null");
    }

  }

  public void property(PropertyNameCoordinate property) {
    if (property == null) {
      throw new IllegalArgumentException("property must not be null");
    }

  }

}
