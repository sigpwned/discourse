/*-
 * =================================LICENSE_START==================================
 * discourse-core
 * ====================================SECTION=====================================
 * Copyright (C) 2022 - 2024 Andy Boothe
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
package com.sigpwned.discourse.core.args;

import static java.util.Objects.requireNonNull;
import java.util.Objects;

public class SwitchName implements Comparable<SwitchName> {
  public static SwitchName fromString(String s) {
    return new SwitchName(s);
  }

  private final String text;

  public SwitchName(String text) {
    this.text = requireNonNull(text);
    if (text.isEmpty())
      throw new IllegalArgumentException("switch name must not be empty");
  }

  public String getText() {
    return text;
  }

  public int length() {
    return text.length();
  }

  @Override
  public int hashCode() {
    return Objects.hash(text);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    SwitchName other = (SwitchName) obj;
    return Objects.equals(text, other.text);
  }

  @Override
  public String toString() {
    return text;
  }

  @Override
  public int compareTo(SwitchName that) {
    return this.toString().compareTo(that.toString());
  }
}
