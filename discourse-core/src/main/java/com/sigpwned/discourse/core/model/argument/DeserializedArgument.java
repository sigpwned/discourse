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
package com.sigpwned.discourse.core.model.argument;

import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.coordinate.Coordinate;
import com.sigpwned.discourse.core.parameter.ConfigurationParameter;

public record DeserializedArgument(Coordinate coordinate, ConfigurationParameter parameter,
    String argumentText, Object argumentValue) {

  public DeserializedArgument {
    coordinate = requireNonNull(coordinate);
    parameter = requireNonNull(parameter);
  }
}
