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
package com.sigpwned.discourse.core;

import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.annotation.OptionParameter;
import com.sigpwned.discourse.core.command.Command;

/**
 * Indicates a problem with the arguments given by the user, e.g. an {@link OptionParameter} was not
 * given a value on the command line. The command line cannot be understood.
 */
public abstract class SyntaxException extends RuntimeException {

  private final Command<?> command;

  protected SyntaxException(Command<?> command, String message) {
    super(message);
    this.command = requireNonNull(command);
  }

  public Command<?> getCommand() {
    return command;
  }
}
