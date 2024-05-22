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
package com.sigpwned.discourse.core.module.scan.syntax.detect;

import java.util.Set;
import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.annotation.PositionalParameter;
import com.sigpwned.discourse.core.args.Coordinate;
import com.sigpwned.discourse.core.args.coordinate.PositionalCoordinate;
import com.sigpwned.discourse.core.invocation.model.SyntaxDetection;
import com.sigpwned.discourse.core.invocation.phase.scan.model.syntax.CandidateSyntax;
import com.sigpwned.discourse.core.invocation.phase.scan.syntax.SyntaxDetector;
import com.sigpwned.discourse.core.util.Maybe;
import com.sigpwned.discourse.core.util.Streams;

public class PositionalSyntaxDetector implements SyntaxDetector {
  public static final PositionalSyntaxDetector INSTANCE = new PositionalSyntaxDetector();

  @Override
  public Maybe<SyntaxDetection> detectSyntax(Class<?> clazz, CandidateSyntax candidate,
      InvocationContext context) {
    PositionalParameter positional = candidate.annotations().stream()
        .mapMulti(Streams.filterAndCast(PositionalParameter.class)).findFirst().orElse(null);
    if (positional == null) {
      // This is fine. Not every candidate is actually syntax.
      return Maybe.maybe();
    }

    boolean required = positional.required();

    Set<Coordinate> coordinates = Set.of(PositionalCoordinate.of(positional.position()));

    return Maybe.yes(new SyntaxDetection(required, false, false, coordinates));
  }
}
