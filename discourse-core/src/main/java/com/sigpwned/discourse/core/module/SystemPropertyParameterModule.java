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
package com.sigpwned.discourse.core.module;

import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.sigpwned.discourse.core.Chain;
import com.sigpwned.discourse.core.Module;
import com.sigpwned.discourse.core.args.Coordinate;
import com.sigpwned.discourse.core.args.Token;
import com.sigpwned.discourse.core.module.parameter.systemproperty.SystemPropertyCoordinate;
import com.sigpwned.discourse.core.module.parameter.systemproperty.SystemPropertyParameter;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener;
import com.sigpwned.discourse.core.pipeline.invocation.step.preprocess.coordinates.CoordinatesPreprocessor;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.SyntaxDetection;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.SyntaxDetector;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.model.CandidateSyntax;
import com.sigpwned.discourse.core.util.Maybe;
import com.sigpwned.discourse.core.util.Streams;

public class SystemPropertyParameterModule extends Module {
  private Set<SystemPropertyCoordinate> coordinates;

  @Override
  public void registerSyntaxDetectors(Chain<SyntaxDetector> chain) {
    chain.addFirst(new SyntaxDetector() {
      @Override
      public Maybe<SyntaxDetection> detectSyntax(Class<?> clazz, CandidateSyntax candidate,
          InvocationContext context) {
        SystemPropertyParameter property = candidate.annotations().stream()
            .mapMulti(Streams.filterAndCast(SystemPropertyParameter.class)).findFirst()
            .orElse(null);
        if (property == null)
          return Maybe.maybe();

        if (property.property().equals("")) {
          // TODO better exception
          throw new IllegalArgumentException("System property name must not be empty");
        }
        Set<Coordinate> coordinates = Set.of(new SystemPropertyCoordinate(property.property()));

        return Maybe.yes(new SyntaxDetection(coordinates));
      }
    });
  }

  @Override
  public void registerCoordinatesPreprocessors(Chain<CoordinatesPreprocessor> chain) {
    chain.addLast(new CoordinatesPreprocessor() {
      @Override
      public Map<Coordinate, String> preprocess(Map<Coordinate, String> originalCoordinates) {
        if (coordinates != null) {
          // Because of the documented order of operations, this should never happen
          throw new IllegalStateException("coordinates already set");
        }

        Set<SystemPropertyCoordinate> coordinates = new HashSet<>();
        for (Map.Entry<Coordinate, String> entry : originalCoordinates.entrySet()) {
          Coordinate coordinate = entry.getKey();
          if (coordinate instanceof SystemPropertyCoordinate env) {
            coordinates.add(env);
          }
        }

        Map<Coordinate, String> preprocessedCoordinates = originalCoordinates;

        SystemPropertyParameterModule.this.coordinates = unmodifiableSet(coordinates);

        return unmodifiableMap(preprocessedCoordinates);
      }
    });
  }

  @Override
  public void registerListeners(Chain<InvocationPipelineListener> chain) {
    chain.addLast(new InvocationPipelineListener() {
      @Override
      public void afterParseStep(List<Token> preprocessedTokens,
          List<Map.Entry<Coordinate, String>> parsedArgs, InvocationContext context) {
        if (coordinates == null) {
          // Because of the documented order of operations, this should never happen
          throw new IllegalStateException("coordinates not set");
        }

        for (SystemPropertyCoordinate coordinate : coordinates) {
          String propertyName = coordinate.getPropertyName();
          String propertyValue = System.getenv(propertyName);
          if (propertyValue != null) {
            parsedArgs.add(Map.entry(coordinate, propertyValue));
          }
        }
      }
    });
  }
}
