package com.sigpwned.discourse.core.coordinate;

import java.util.Comparator;
import java.util.Objects;
import com.sigpwned.discourse.core.Coordinate;
import com.sigpwned.discourse.core.coordinate.name.PropertyNameCoordinate;
import com.sigpwned.discourse.core.coordinate.name.SwitchNameCoordinate;
import com.sigpwned.discourse.core.coordinate.name.VariableNameCoordinate;

public abstract class NameCoordinate extends Coordinate implements Comparable<NameCoordinate> {
  public static enum Type {
    VARIABLE, PROPERTY, SWITCH;
  }

  private final Type type;
  private final String text;

  protected NameCoordinate(Type type, String text) {
    super(Family.NAME);
    if (type == null)
      throw new NullPointerException();
    if (text == null)
      throw new NullPointerException();
    this.type = type;
    this.text = text;
  }

  /**
   * @return the type
   */
  public Type getType() {
    return type;
  }

  /**
   * @return the text
   */
  public String getText() {
    return text;
  }

  public VariableNameCoordinate asVariable() {
    return (VariableNameCoordinate) this;
  }

  public PropertyNameCoordinate asProperty() {
    return (PropertyNameCoordinate) this;
  }

  public SwitchNameCoordinate asSwitch() {
    return (SwitchNameCoordinate) this;
  }

  @Override
  public int hashCode() {
    return Objects.hash(text, type);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    NameCoordinate other = (NameCoordinate) obj;
    return Objects.equals(text, other.text) && type == other.type;
  }

  @Override
  public String toString() {
    return getText();
  }
  
  public static final Comparator<NameCoordinate> COMPARATOR=Comparator.comparing(NameCoordinate::getText);

  @Override
  public int compareTo(NameCoordinate that) {
    return COMPARATOR.compare(this, that);
  }
}
