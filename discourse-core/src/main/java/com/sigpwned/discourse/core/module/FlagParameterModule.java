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

import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.sigpwned.discourse.core.Chain;
import com.sigpwned.discourse.core.Module;
import com.sigpwned.discourse.core.args.Coordinate;
import com.sigpwned.discourse.core.args.SwitchName;
import com.sigpwned.discourse.core.args.Token;
import com.sigpwned.discourse.core.args.coordinate.OptionCoordinate;
import com.sigpwned.discourse.core.args.token.SwitchNameToken;
import com.sigpwned.discourse.core.args.token.ValueToken;
import com.sigpwned.discourse.core.module.parameter.flag.FlagCoordinate;
import com.sigpwned.discourse.core.module.parameter.flag.FlagParameter;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;
import com.sigpwned.discourse.core.pipeline.invocation.step.preprocess.coordinates.CoordinatesPreprocessor;
import com.sigpwned.discourse.core.pipeline.invocation.step.preprocess.tokens.TokensPreprocessor;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.SyntaxDetection;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.SyntaxDetector;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.model.CandidateSyntax;
import com.sigpwned.discourse.core.util.Maybe;
import com.sigpwned.discourse.core.util.Streams;

public class FlagParameterModule extends Module {
  private Set<SwitchName> flags;

  @Override
  public void registerSyntaxDetectors(Chain<SyntaxDetector> chain) {
    chain.addLast(new SyntaxDetector() {
      @Override
      public Maybe<SyntaxDetection> detectSyntax(Class<?> clazz, CandidateSyntax candidate,
          InvocationContext context) {
        FlagParameter flag = candidate.annotations().stream()
            .mapMulti(Streams.filterAndCast(FlagParameter.class)).findFirst().orElse(null);
        if (flag == null)
          return Maybe.maybe();

        Set<Coordinate> coordinates = new HashSet<>(2);
        if (!flag.longName().equals("")) {
          coordinates.add(new FlagCoordinate(SwitchName.fromString(flag.longName())));
        }
        if (!flag.shortName().equals("")) {
          coordinates.add(new FlagCoordinate(SwitchName.fromString(flag.shortName())));
        }
        if (coordinates.isEmpty()) {
          // TODO better exception
          throw new IllegalArgumentException(
              "@FlagParameter must have at least one of longName or shortName");
        }

        return Maybe.yes(new SyntaxDetection(false, flag.help(), flag.version(), coordinates));
      }
    });
  }

  @Override
  public void registerTokensPreprocessors(Chain<TokensPreprocessor> chain) {
    // TODO Where should I get the flags from?
    chain.addLast(new TokensPreprocessor() {
      @Override
      public List<Token> preprocessTokens(List<Token> tokens) {
        if (flags == null) {
          // Because of the documented order of operations, this should never happen.
          throw new IllegalStateException("flags not set");
        }
        List<Token> result = new ArrayList<>(tokens.size());
        for (Token token : tokens) {
          result.add(token);
          if (token instanceof SwitchNameToken switchNameToken) {
            SwitchName switchName = switchNameToken.getName();
            if (flags.contains(switchName)) {
              result.add(new ValueToken("true", false));
            }
          }
        }
        return unmodifiableList(result);
      }
    });
  }

  @Override
  public void registerCoordinatesPreprocessors(Chain<CoordinatesPreprocessor> chain) {
    chain.addLast(new CoordinatesPreprocessor() {
      @Override
      public Map<Coordinate, String> preprocess(Map<Coordinate, String> coordinates) {
        if (flags != null) {
          // Because of the documented order of operations, this should never happen.
          throw new IllegalStateException("flags already set");
        }

        Set<SwitchName> flags = new HashSet<>();
        Map<Coordinate, String> preprocessedCoordinates = new HashMap<>();
        for (Map.Entry<Coordinate, String> entry : coordinates.entrySet()) {
          Coordinate coordinate = entry.getKey();
          String propertyName = entry.getValue();
          if (coordinate instanceof FlagCoordinate flag) {
            flags.add(flag.getName());
            preprocessedCoordinates.put(new OptionCoordinate(flag.getName()),
                propertyName);
          } else {
            preprocessedCoordinates.put(coordinate, propertyName);
          }
        }

        FlagParameterModule.this.flags = flags;

        return unmodifiableMap(preprocessedCoordinates);
      }
    });
  }
}
