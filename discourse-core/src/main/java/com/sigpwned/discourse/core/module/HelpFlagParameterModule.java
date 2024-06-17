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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.sigpwned.discourse.core.Chain;
import com.sigpwned.discourse.core.Dialect;
import com.sigpwned.discourse.core.Module;
import com.sigpwned.discourse.core.annotation.HelpFlagParameter;
import com.sigpwned.discourse.core.args.Coordinate;
import com.sigpwned.discourse.core.args.SwitchName;
import com.sigpwned.discourse.core.command.resolved.ResolvedCommand;
import com.sigpwned.discourse.core.error.ExitError;
import com.sigpwned.discourse.core.format.HelpFormatter;
import com.sigpwned.discourse.core.module.parameter.flag.help.HelpFlagCoordinate;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineListener;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationPipelineStep;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.SyntaxDetection;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.SyntaxDetector;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.model.CandidateSyntax;
import com.sigpwned.discourse.core.util.Maybe;
import com.sigpwned.discourse.core.util.Streams;

public class HelpFlagParameterModule extends Module {
  private Set<String> propertyNames;

  @Override
  public void registerSyntaxDetectors(Chain<SyntaxDetector> chain) {
    chain.addLast(new SyntaxDetector() {
      @Override
      public Maybe<SyntaxDetection> detectSyntax(Class<?> clazz, CandidateSyntax candidate,
          InvocationContext context) {
        HelpFlagParameter flag = candidate.annotations().stream()
            .mapMulti(Streams.filterAndCast(HelpFlagParameter.class)).findFirst().orElse(null);
        if (flag == null)
          return Maybe.maybe();

        Set<Coordinate> coordinates = new HashSet<>(2);
        if (!flag.longName().equals("")) {
          coordinates.add(new HelpFlagCoordinate(SwitchName.fromString(flag.longName())));
        }
        if (!flag.shortName().equals("")) {
          coordinates.add(new HelpFlagCoordinate(SwitchName.fromString(flag.shortName())));
        }
        if (coordinates.isEmpty()) {
          // TODO better exception
          throw new IllegalArgumentException(
              "@HelpFlagParameter must have at least one of longName or shortName");
        }

        return Maybe.yes(new SyntaxDetection(coordinates));
      }
    });
  }

  @Override
  public void registerListeners(Chain<InvocationPipelineListener> chain) {
    chain.addLast(new InvocationPipelineListener() {
      @Override
      public void beforePreprocessCoordinatesStep(Map<Coordinate, String> originalCoordinates,
          InvocationContext context) {
        if (propertyNames != null) {
          // Because of the documented order of operations, this should never happen.
          throw new IllegalStateException("propertyNames already set");
        }

        Set<String> propertyNames = new HashSet<>();
        for (Map.Entry<Coordinate, String> entry : originalCoordinates.entrySet()) {
          Coordinate coordinate = entry.getKey();
          String propertyName = entry.getValue();
          if (coordinate instanceof HelpFlagCoordinate) {
            propertyNames.add(propertyName);
          }
        }

        HelpFlagParameterModule.this.propertyNames = propertyNames;
      }

      @Override
      public void afterMapStep(Map<String, List<String>> groupedArgs,
          Map<String, List<Object>> mappedArgs, InvocationContext context) {
        if (propertyNames == null) {
          // Because of the documented order of operations, this should never happen.
          throw new IllegalStateException("propertyNames not set");
        }

        for (String propertyName : propertyNames) {
          List<Object> mappedArgValues = mappedArgs.get(propertyName);
          if (mappedArgValues != null && !mappedArgValues.isEmpty()) {
            Object mappedArgValue = mappedArgValues.get(mappedArgValues.size() - 1);
            if (mappedArgValue instanceof Boolean b && b.booleanValue()) {
              HelpFormatter formatter =
                  context.get(InvocationPipelineStep.HELP_FORMATTER_KEY).orElseThrow();

              Dialect dialect = context.get(InvocationPipelineStep.DIALECT_KEY).orElseThrow();

              ResolvedCommand<?> command =
                  context.get(InvocationPipelineStep.RESOLVED_COMMAND_KEY).orElseThrow();

              ExitError.Factory exit =
                  context.get(InvocationPipelineStep.EXIT_ERROR_FACTORY_KEY).orElseThrow();

              formatter.formatHelp(dialect, command, context);

              throw exit.createExitError(0);
            }
          }
        }
      }
    });
  }

  @Override
  public List<Module> getDependencies() {
    return List.of(new FlagParameterModule());
  }
}
