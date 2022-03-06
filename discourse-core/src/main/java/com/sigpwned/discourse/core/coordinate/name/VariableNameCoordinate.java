package com.sigpwned.discourse.core.coordinate.name;

import com.sigpwned.discourse.core.coordinate.NameCoordinate;

public class VariableNameCoordinate extends NameCoordinate {
  public static VariableNameCoordinate fromString(String text) {
    return new VariableNameCoordinate(text);
  }
  
  public VariableNameCoordinate(String text) {
    super(Type.VARIABLE, text);
    if (text.isEmpty())
      throw new IllegalArgumentException("variable names must not be blank");
  }
}
