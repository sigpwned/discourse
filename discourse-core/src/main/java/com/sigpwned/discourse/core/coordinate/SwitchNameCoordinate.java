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

/**
 * A coordinate that represents a switch name, e.g., -x or --foo
 */
public abstract sealed class SwitchNameCoordinate extends NameCoordinate permits
    LongSwitchNameCoordinate, ShortSwitchNameCoordinate {

  public static SwitchNameCoordinate fromSwitchString(String s) {
    if (s.startsWith(LongSwitchNameCoordinate.PREFIX)) {
      return new LongSwitchNameCoordinate(
          s.substring(LongSwitchNameCoordinate.PREFIX.length(), s.length()));
    } else if (s.startsWith(ShortSwitchNameCoordinate.PREFIX)) {
      return new ShortSwitchNameCoordinate(
          s.substring(ShortSwitchNameCoordinate.PREFIX.length(), s.length()));
    } else {
      throw new IllegalArgumentException("invalid switch string: " + s);
    }
  }

  protected SwitchNameCoordinate(String text) {
    super(text);
  }

  public abstract String toSwitchString();

  public String toString() {
    return "switch " + toSwitchString();
  }
}
