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
package com.sigpwned.discourse.core.exception.configuration;

import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.exception.ConfigurationException;

public class InvalidPositionConfigurationException extends ConfigurationException {

  private final String parameterName;
  private final int position;

  public InvalidPositionConfigurationException(String parameterName, int position) {
    super("Parameter %s has invalid position %d".formatted(parameterName, position));
    this.parameterName = requireNonNull(parameterName);
    this.position = position;
  }
  
  public String getParameterName() {
    return parameterName;
  }

  /**
   * @return the missingPosition
   */
  public int getPosition() {
    return position;
  }
}
