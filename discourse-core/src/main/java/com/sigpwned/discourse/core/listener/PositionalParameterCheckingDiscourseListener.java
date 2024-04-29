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
package com.sigpwned.discourse.core.listener;

import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.command.CommandWalker;
import com.sigpwned.discourse.core.command.SingleCommand;
import com.sigpwned.discourse.core.coordinate.PositionCoordinate;
import com.sigpwned.discourse.core.exception.configuration.DuplicateCoordinateConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.InvalidCollectionParameterPlacementConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.InvalidRequiredParameterPlacementConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.MissingPositionConfigurationException;
import com.sigpwned.discourse.core.model.command.Discriminator;
import com.sigpwned.discourse.core.parameter.PositionalConfigurationParameter;
import com.sigpwned.discourse.core.util.Streams;
import java.util.Comparator;
import java.util.List;

public class PositionalParameterCheckingDiscourseListener implements DiscourseListener {

  public static final PositionalParameterCheckingDiscourseListener INSTANCE = new PositionalParameterCheckingDiscourseListener();

  @Override
  public <T> void afterScan(Command<T> rootCommand, InvocationContext context) {
    checkPositions(rootCommand);
  }

  /**
   * Make sure positional parameters are valid. We break this out into a separate method so that it
   * can be overridden by subclasses without worrying about where it is called in the listener
   * lifecycle.
   */
  protected <T> void checkPositions(Command<T> command) {
    new CommandWalker().walk(command, new CommandWalker.Visitor<T>() {
      @Override
      public void singleCommand(Discriminator discriminator, SingleCommand<? extends T> command) {
        boolean optional = false;
        List<PositionalConfigurationParameter> positionalParameters = command.getParameters()
            .stream().mapMulti(Streams.filterAndCast(PositionalConfigurationParameter.class))
            .sorted(Comparator.comparing(PositionalConfigurationParameter::getPosition)).toList();
        for (int i = 0; i < positionalParameters.size(); i++) {
          PositionalConfigurationParameter parameter = positionalParameters.get(i);
          PositionCoordinate coordinate = parameter.getPosition();
          int position = coordinate.getIndex();

          if (position > i) {
            if (i == 0) {
              throw new MissingPositionConfigurationException(0);
            } else {
              throw new MissingPositionConfigurationException(
                  positionalParameters.get(i - 1).getPosition().getIndex() + 1);
            }
          } else if (position < i) {
            throw new DuplicateCoordinateConfigurationException(coordinate);
          }

          if (parameter.isCollection() && i < positionalParameters.size() - 1) {
            // If the parameter is a collection, it must be the last positional parameter. Otherwise,
            // it would "eat" parameters that should be consumed by subsequent positional parameters.
            throw new InvalidCollectionParameterPlacementConfigurationException(i);
          }

          if (optional && parameter.isRequired()) {
            throw new InvalidRequiredParameterPlacementConfigurationException(i);
          }

          if (!parameter.isRequired()) {
            optional = true;
          }
        }
      }
    });
  }
}
