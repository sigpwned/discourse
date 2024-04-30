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

import java.util.regex.Pattern;

/**
 * A {@link NameCoordinate} that represents a system property name.
 *
 * @see System#getProperty(String)
 */
public final class PropertyNameCoordinate extends NameCoordinate {

  public static final Pattern PATTERN = Pattern.compile("[a-zA-Z0-9_.-]+");

  public static PropertyNameCoordinate fromString(String s) {
    return new PropertyNameCoordinate(s);
  }

  public PropertyNameCoordinate(String text) {
    super(text);
    if (text.isEmpty()) {
      throw new IllegalArgumentException("property names must not be blank");
    }
    if (!PATTERN.matcher(text).matches()) {
      throw new IllegalArgumentException("invalid property name: " + text);
    }
  }

  @Override
  public String toString() {
    return "system property " + getText();
  }
}
