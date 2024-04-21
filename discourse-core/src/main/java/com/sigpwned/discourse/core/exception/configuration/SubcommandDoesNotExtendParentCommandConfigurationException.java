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

import static java.lang.String.format;
import com.sigpwned.discourse.core.exception.ConfigurationException;

public class SubcommandDoesNotExtendParentCommandConfigurationException extends ConfigurationException {
  private final Class<?> commandType;
  private final Class<?> subcommandType;

  public SubcommandDoesNotExtendParentCommandConfigurationException(Class<?> commandType,
      Class<?> subcommandType) {
    super(format("Subcommand configurable %s does not extend root configurable %s",
        subcommandType.getName(), commandType.getName()));
    this.commandType = commandType;
    this.subcommandType = subcommandType;
  }

  /**
   * @return the rawType
   */
  public Class<?> getRawType() {
    return commandType;
  }

  /**
   * @return the subcommandType
   */
  public Class<?> getSubcommandType() {
    return subcommandType;
  }
}
