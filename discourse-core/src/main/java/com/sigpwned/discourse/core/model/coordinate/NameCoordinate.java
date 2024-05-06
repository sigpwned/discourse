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
package com.sigpwned.discourse.core.model.coordinate;

import java.util.Comparator;
import java.util.Objects;

/**
 * A {@link Coordinate} with an explicit name, like {@code -x} or {@code --xray}.
 */
public abstract sealed class NameCoordinate extends Coordinate implements
    Comparable<NameCoordinate> permits PropertyNameCoordinate, SwitchNameCoordinate,
    VariableNameCoordinate {

  private final String text;

  protected NameCoordinate(String text) {
    if (text == null) {
      throw new NullPointerException();
    }
    this.text = text;
  }

  /**
   * @return the text
   */
  public String getText() {
    return text;
  }

  @Override
  public int hashCode() {
    return Objects.hash(text);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    NameCoordinate other = (NameCoordinate) obj;
    return Objects.equals(text, other.text);
  }

  public static final Comparator<NameCoordinate> COMPARATOR = Comparator.comparing(
      NameCoordinate::getText);

  @Override
  public int compareTo(NameCoordinate that) {
    return COMPARATOR.compare(this, that);
  }
}
