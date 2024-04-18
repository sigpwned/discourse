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
import com.sigpwned.discourse.core.Discriminator;
import com.sigpwned.discourse.core.command.MultiCommand;

/**
 * Thrown when a user provides a discriminator that does not match any subcommand. For example, if a
 * command defines two subcommands "foo" and "bar", but the user provides the discriminator "baz",
 * then this exception would be thrown with the value "baz".
 */
public class UnrecognizedSubcommandArgumentException extends ConfigurationException {

  private final MultiCommand<?> command;
  private final Discriminator discriminator;

  /**
   * @param command       The context in which the unrecognized discriminator was provided.
   * @param discriminator The discriminator that was provided.
   */
  public UnrecognizedSubcommandArgumentException(MultiCommand<?> command,
      Discriminator discriminator) {
    super(format("There is no subcommand for discriminator '%s'", discriminator));
    this.command = requireNonNull(command);
    this.discriminator = requireNonNull(discriminator);
  }

  /**
   * @return the command
   */
  public MultiCommand<?> getCommand() {
    return command;
  }

  /**
   * @return the invalidDiscriminator
   */
  public Discriminator getInvalidDiscriminator() {
    return discriminator;
  }
}
