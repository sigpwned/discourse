package com.sigpwned.discourse.core.coordinate.name;

import com.sigpwned.discourse.core.coordinate.NameCoordinate;

public class PropertyNameCoordinate extends NameCoordinate {
  public static PropertyNameCoordinate fromString(String s) {
    return new PropertyNameCoordinate(s);
  }
  
  public PropertyNameCoordinate(String text) {
    super(Type.PROPERTY, text);
    if (text.isEmpty())
      throw new IllegalArgumentException("property names must not be blank");
  }
}
