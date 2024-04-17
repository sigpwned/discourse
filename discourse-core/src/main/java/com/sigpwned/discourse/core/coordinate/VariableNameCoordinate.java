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
 * A coordinate that represents an environment variable name.
 */
public final class VariableNameCoordinate extends NameCoordinate {

  public static VariableNameCoordinate fromString(String text) {
    return new VariableNameCoordinate(text);
  }

  public VariableNameCoordinate(String text) {
    super(text);
    if (text.isEmpty()) {
      throw new IllegalArgumentException("variable names must not be blank");
    }
  }
}