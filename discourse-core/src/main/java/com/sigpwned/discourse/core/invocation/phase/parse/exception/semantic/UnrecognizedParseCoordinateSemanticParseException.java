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
package com.sigpwned.discourse.core.invocation.phase.parse.exception.semantic;

import com.sigpwned.discourse.core.args.Coordinate;
import com.sigpwned.discourse.core.invocation.phase.parse.exception.SemanticParseException;

/**
 * Thrown when a parse coordinate cannot be mapped to a known property name. For example, this
 * exception would be thrown with value <code>@1</code> if an application expects 1 positional
 * argument but receives 2 because the user provided an extra argument at position 2.
 */
public class UnrecognizedParseCoordinateSemanticParseException extends SemanticParseException {
  private static final long serialVersionUID = 8921665531829244368L;

  private final Coordinate coordinate;

  public UnrecognizedParseCoordinateSemanticParseException(Coordinate coordinate) {
    super("unrecognized parse coordinate %s".formatted(coordinate));
    this.coordinate = coordinate;
  }

  public Coordinate getCoordinate() {
    return coordinate;
  }
}
