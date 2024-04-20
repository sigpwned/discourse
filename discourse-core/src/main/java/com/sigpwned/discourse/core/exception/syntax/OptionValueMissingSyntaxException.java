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

import com.sigpwned.discourse.core.SyntaxException;
import com.sigpwned.discourse.core.command.SingleCommand;
import com.sigpwned.discourse.core.coordinate.SwitchNameCoordinate;

/**
 * Thrown when an option parameter -- which always requires a value -- is not given a value
 */
public class OptionValueMissingSyntaxException extends SyntaxException {

  private final String parameterName;
  private final SwitchNameCoordinate coordinate;

  public OptionValueMissingSyntaxException(SingleCommand<?> command, String parameterName,
      SwitchNameCoordinate coordinate) {
    super(command, "Option %s requires value".formatted(coordinate.toSwitchString()));
    this.parameterName = requireNonNull(parameterName);
    this.coordinate = requireNonNull(coordinate);
  }

  /**
   * @return the parameterName
   */
  public String getParameterName() {
    return parameterName;
  }

  /**
   * @return the longName
   */
  public SwitchNameCoordinate getCoordinate() {
    return coordinate;
  }

  /**
   * The first command that was being parsed when the exception was thrown.
   *
   * @return the command
   */
  @Override
  public SingleCommand<?> getCommand() {
    return (SingleCommand<?>) super.getCommand();
  }
}