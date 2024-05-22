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
 * Thrown when a switch is not recognized. For example, this exception would be thrown if an
 * application expects switches {@code -a} and {@code -b}, but the user provides a switch
 * {@code -c}.
 */
public class NoSuchSwitchSyntaxParseException extends SyntaxParseException {
  private static final long serialVersionUID = -5704560154794184050L;

  private final String unrecognizedSwitchName;

  public NoSuchSwitchSyntaxParseException(String unrecognizedSwitchName) {
    super("unrecognized switch %s".formatted(unrecognizedSwitchName));
    this.unrecognizedSwitchName = requireNonNull(unrecognizedSwitchName);
  }

  public String getFlagSwitchName() {
    return unrecognizedSwitchName;
  }
}
