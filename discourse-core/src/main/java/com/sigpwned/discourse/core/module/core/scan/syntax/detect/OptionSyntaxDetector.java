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
package com.sigpwned.discourse.core.module.core.scan.syntax.detect;

import java.util.HashSet;
import java.util.Set;
import com.sigpwned.discourse.core.annotation.OptionParameter;
import com.sigpwned.discourse.core.args.Coordinate;
import com.sigpwned.discourse.core.args.SwitchName;
import com.sigpwned.discourse.core.args.coordinate.OptionCoordinate;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.SyntaxDetection;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.SyntaxDetector;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.model.CandidateSyntax;
import com.sigpwned.discourse.core.util.Maybe;
import com.sigpwned.discourse.core.util.Streams;

public class OptionSyntaxDetector implements SyntaxDetector {
  public static final OptionSyntaxDetector INSTANCE = new OptionSyntaxDetector();

  @Override
  public Maybe<SyntaxDetection> detectSyntax(Class<?> clazz, CandidateSyntax candidate,
      InvocationContext context) {
    OptionParameter option = candidate.annotations().stream()
        .mapMulti(Streams.filterAndCast(OptionParameter.class)).findFirst().orElse(null);
    if (option == null) {
      // This is fine. Not every candidate is actually syntax.
      return Maybe.maybe();
    }

    boolean required = option.required();

    Set<Coordinate> coordinates = new HashSet<>(2);
    if (!option.longName().equals("")) {
      coordinates.add(new OptionCoordinate(SwitchName.fromString(option.longName())));
    }
    if (!option.shortName().equals("")) {
      coordinates.add(new OptionCoordinate(SwitchName.fromString(option.shortName())));
    }

    if (coordinates.isEmpty()) {
      // TODO better exception
      throw new IllegalArgumentException("option must have a long or short name");
    }

    return Maybe.yes(new SyntaxDetection(required, false, false, coordinates));
  }
}
