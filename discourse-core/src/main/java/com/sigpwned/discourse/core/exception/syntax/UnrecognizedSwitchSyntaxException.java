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

import static java.lang.String.*;
import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.SyntaxException;
import com.sigpwned.discourse.core.command.SingleCommand;
import com.sigpwned.discourse.core.coordinate.SwitchNameCoordinate;

/**
 * Thrown when a long name is not recognized. For example, if a command only defines the long names
 * "alpha" and "bravo", but the user provides "--charlie", then this exception would be thrown with
 * the long name "charlie".
 */
public class UnrecognizedSwitchSyntaxException extends SyntaxException {

  private final SwitchNameCoordinate coordinate;

  public UnrecognizedSwitchSyntaxException(SingleCommand<?> command,
      SwitchNameCoordinate coordinate) {
    super(command, format("Unrecognized switch %s", coordinate));
    this.coordinate = requireNonNull(coordinate);
  }

  /**
   * @return the longName
   */
  public SwitchNameCoordinate getCoordinate() {
    return coordinate;
  }

  /**
   * The command that was being parsed when the exception was thrown.
   *
   * @return the command
   */
  @Override
  public SingleCommand<?> getCommand() {
    return (SingleCommand<?>) super.getCommand();
  }
}
