/*-
 * =================================LICENSE_START==================================
 * discourse-core
 * ====================================SECTION=====================================
 * Copyright (C) 2022 - 2024 Andy Boothe
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
package com.sigpwned.discourse.core.invocation.phase.parse.exception.syntax;

import static java.util.Objects.requireNonNull;
import com.sigpwned.discourse.core.invocation.phase.parse.exception.SyntaxParseException;

/**
 * Thrown when an option requires a value, but none was given. For example, this exception would be
 * thrown if an option {@code -b} were specified in the middle of a bundle as in {@code -abc}.
 * Options always require values, by definition.
 */
public class OptionValueMissingSyntaxParseException extends SyntaxParseException {
  private static final long serialVersionUID = -546183389765696066L;

  private final String optionSwitchName;

  public OptionValueMissingSyntaxParseException(String optionSwitchName) {
    super("option %s requires value, but none was given".formatted(optionSwitchName));
    this.optionSwitchName = requireNonNull(optionSwitchName);
  }

  public String getOptionSwitchName() {
    return optionSwitchName;
  }
}
