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
import com.sigpwned.discourse.core.module.parameter.env.EnvironmentVariableCoordinate;
import com.sigpwned.discourse.core.module.parameter.env.EnvironmentVariableParameter;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener;
import com.sigpwned.discourse.core.pipeline.invocation.step.preprocess.coordinates.CoordinatesPreprocessor;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.SyntaxDetection;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.SyntaxDetector;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.model.CandidateSyntax;
import com.sigpwned.discourse.core.util.Maybe;
import com.sigpwned.discourse.core.util.Streams;

public class EnvironmentVariableParameterModule extends Module {
  private Set<EnvironmentVariableCoordinate> coordinates;

  @Override
  public void registerSyntaxDetectors(Chain<SyntaxDetector> chain) {
    chain.addFirst(new SyntaxDetector() {
      @Override
      public Maybe<SyntaxDetection> detectSyntax(Class<?> clazz, CandidateSyntax candidate,
          InvocationContext context) {
        EnvironmentVariableParameter variable = candidate.annotations().stream()
            .mapMulti(Streams.filterAndCast(EnvironmentVariableParameter.class)).findFirst()
            .orElse(null);
        if (variable == null)
          return Maybe.maybe();

        if (variable.variable().equals("")) {
          // TODO better exception
          throw new IllegalArgumentException("Environment variable name must not be empty");
        }
        Set<Coordinate> coordinates =
            Set.of(new EnvironmentVariableCoordinate(variable.variable()));

        return Maybe.yes(new SyntaxDetection(false, false, false, coordinates));
      }
    });
  }

  @Override
  public void registerListeners(Chain<InvocationPipelineListener> chain) {
    chain.addLast(new InvocationPipelineListener() {
      @Override
      public void afterParseStep(List<Token> preprocessedTokens,
          List<Map.Entry<Coordinate, String>> parsedArgs) {
        if (coordinates == null) {
          // Because of the documented order of operations, this should never happen
          throw new IllegalStateException("coordinates not set");
        }

        for (EnvironmentVariableCoordinate coordinate : coordinates) {
          String variableName = coordinate.getVariableName();
          String variableValue = System.getenv(variableName);
          if (variableValue != null) {
            parsedArgs.add(Map.entry(coordinate, variableValue));
          }
        }
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

        Set<EnvironmentVariableCoordinate> coordinates = new HashSet<>();
        for (Map.Entry<Coordinate, String> entry : originalCoordinates.entrySet()) {
          Coordinate coordinate = entry.getKey();
          if (coordinate instanceof EnvironmentVariableCoordinate env) {
            coordinates.add(env);
          }
        }

        Map<Coordinate, String> preprocessedCoordinates = originalCoordinates;

        EnvironmentVariableParameterModule.this.coordinates = unmodifiableSet(coordinates);

        return unmodifiableMap(preprocessedCoordinates);
      }
    });
  }
}
