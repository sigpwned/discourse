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
package com.sigpwned.discourse.core.exception.argument;

import static java.lang.String.*;
import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.ConfigurationException;
import com.sigpwned.discourse.core.command.MultiCommand;

/**
 * Thrown when a user provides a command line argument in a position where the application expects a
 * discriminator, but the argument is not a valid discriminator.
 */
public class InvalidDiscriminatorArgumentException extends ConfigurationException {

  private final MultiCommand<?> command;
  private final String invalidDiscriminator;

  public InvalidDiscriminatorArgumentException(MultiCommand<?> command,
      String invalidDiscriminator) {
    super(format("The string '%s' is not a valid subcommand", invalidDiscriminator));
    this.command = requireNonNull(command);
    this.invalidDiscriminator = requireNonNull(invalidDiscriminator);
  }

  /**
   * @return the invalidDiscriminator
   */
  public String getInvalidDiscriminator() {
    return invalidDiscriminator;
  }

  public MultiCommand<?> getCommand() {
    return command;
  }
}
