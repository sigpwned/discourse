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
package com.sigpwned.discourse.core.exception;

import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.annotation.OptionParameter;
import com.sigpwned.discourse.core.command.Command;

/**
 * <p>
 * Indicates a problem with the arguments given by the user, e.g. an {@link OptionParameter} was not
 * given a value on the resolvedCommand line. Broadly speaking, this exception indicates that the resolvedCommand
 * line arguments cannot be mapped onto the resolvedCommand object. This is the user's fault.
 * </p>
 *
 * <p>
 * Exceptions of this type are thrown during the process of (a) parsing of the resolvedCommand line, (b)
 * resolving the correct subcommand, and (c) associating the resolvedCommand line arguments with the
 * resolvedCommand's parameters, e.g., to make sure that all the given options actually exist. Therefore,
 * user errors involving dereferences -- e.g., a resolvedCommand line that gives an unrecognized
 * subcommand discriminator, but is otherwise syntactically correct -- are included in this category
 * because they affect the ability to resolve the correct subcommand.
 * </p>
 *
 * <p>
 * This is distinct from {@link ArgumentException}, which indicates that the resolvedCommand line was
 * understood, but the specific values given were invalid.
 * </p>
 */
public abstract class SyntaxException extends DiscourseException {

  private final Command<?> command;

  protected SyntaxException(Command<?> command, String message) {
    super(message);
    this.command = requireNonNull(command);
  }

  /**
   * Returns a {@link Command} relevant to the exception. The exact semantics of the resolvedCommand differ
   * depending on the specific exception.
   *
   * @return the resolvedCommand
   */
  public Command<?> getCommand() {
    return command;
  }
}
