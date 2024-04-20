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

import com.sigpwned.discourse.core.SyntaxException;
import com.sigpwned.discourse.core.command.MultiCommand;

/**
 * Thrown when the application does not provide sufficient disciminators to dereference a multi
 * command. For example, if a command defines two subcommands "foo" and "bar", but the user provides
 * no discriminators to indicate which subcommand to run, then this exception would be thrown.
 */
public class InsufficientDiscriminatorsSyntaxException extends SyntaxException {

  /**
   * @param command The first {@link MultiCommand} that was not dereferenced
   */
  public InsufficientDiscriminatorsSyntaxException(MultiCommand<?> command) {
    super(command, "Insufficient discriminators given");
  }

  /**
   * The command that was not dereferenced.
   *
   * @return the command
   */
  @Override
  public MultiCommand<?> getCommand() {
    return (MultiCommand<?>) super.getCommand();
  }
}
