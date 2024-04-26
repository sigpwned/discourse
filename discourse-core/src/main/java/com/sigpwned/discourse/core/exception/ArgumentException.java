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

import com.sigpwned.discourse.core.command.SingleCommand;

/**
 * An exception that is thrown when an argument is invalid. That is, the command line was parsed and
 * understood, but the specific value of the argument was not valid. This is the user's fault.
 */
public abstract class ArgumentException extends DiscourseException {

  private final SingleCommand<?> command;

  protected ArgumentException(SingleCommand<?> command, String message) {
    super(message);
    this.command = requireNonNull(command);
  }

  protected ArgumentException(SingleCommand<?> command, String message, Throwable cause) {
    super(message, cause);
    this.command = requireNonNull(command);
  }

  public SingleCommand<?> getCommand() {
    return command;
  }
}
