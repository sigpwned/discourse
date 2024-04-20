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
package com.sigpwned.discourse.core.exception.syntax;

import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.Discriminator;
import com.sigpwned.discourse.core.SyntaxException;
import com.sigpwned.discourse.core.command.MultiCommand;

/**
 * Thrown when a user provides a command line argument in a position where the application expects a
 * discriminator, but the argument is not a valid discriminator. A discriminator must match the
 * pattern "[a-zA-Z0-9][-a-zA-Z0-9_]*".
 *
 * @see Discriminator#PATTERN
 */
public class InvalidDiscriminatorSyntaxException extends SyntaxException {

  private final String invalidDiscriminator;

  public InvalidDiscriminatorSyntaxException(MultiCommand<?> command, String invalidDiscriminator) {
    super(command, "The string '%s' is not a valid discriminator".formatted(invalidDiscriminator));
    this.invalidDiscriminator = requireNonNull(invalidDiscriminator);
  }

  /**
   * @return the invalidDiscriminator
   */
  public String getInvalidDiscriminator() {
    return invalidDiscriminator;
  }

  /**
   * The command that the discriminator would have dereferenced if it were valid.
   *
   * @return the command
   */
  @Override
  public MultiCommand<?> getCommand() {
    return (MultiCommand<?>) super.getCommand();
  }
}
