/*-
 * =================================LICENSE_START==================================
 * discourse-core
 * ====================================SECTION=====================================
 * Copyright (C) 2022 Andy Boothe
 * ====================================SECTION=====================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ==================================LICENSE_END===================================
 */
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
