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

import static java.lang.String.*;

import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.exception.ConfigurationException;

/**
 * An exception thrown when a positional parameter is missing. For example, if a {@link Command}
 * defines parameters for positions 0, 1, and 3, but not 2, then this exception would be thrown for
 * position 2.
 */
public class MissingPositionConfigurationException extends ConfigurationException {

  private final int missingPosition;

  public MissingPositionConfigurationException(int missingPosition) {
    super(format("Missing positional parameter for position %d", missingPosition));
    this.missingPosition = missingPosition;
  }

  /**
   * @return the missingPosition
   */
  public int getMissingPosition() {
    return missingPosition;
  }
}
