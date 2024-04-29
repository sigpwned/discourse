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
import com.sigpwned.discourse.core.coordinate.LongSwitchNameCoordinate;
import com.sigpwned.discourse.core.coordinate.ShortSwitchNameCoordinate;
import com.sigpwned.discourse.core.exception.configuration.DuplicateCoordinateConfigurationException;
import com.sigpwned.discourse.core.model.command.Discriminator;
import com.sigpwned.discourse.core.util.Streams;
import java.util.List;

public class NamedParameterCheckingDiscourseListener implements DiscourseListener {

  public static final NamedParameterCheckingDiscourseListener INSTANCE = new NamedParameterCheckingDiscourseListener();

  @Override
  public <T> void afterScan(Command<T> rootCommand, InvocationContext context) {
    checkNames(rootCommand);
  }

  /**
   * Make sure named parameters are valid. We break this out into a separate method so that it can
   * be overridden by subclasses without worrying about where it is called in the listener
   * lifecycle.
   */
  protected <T> void checkNames(Command<T> command) {
    new CommandWalker().walk(command, new CommandWalker.Visitor<T>() {
      @Override
      public void singleCommand(Discriminator discriminator, SingleCommand<? extends T> command) {
        List<ShortSwitchNameCoordinate> shortNames = command.getParameters().stream()
            .flatMap(p -> p.getCoordinates().stream())
            .mapMulti(Streams.filterAndCast(ShortSwitchNameCoordinate.class)).toList();
        if (Streams.duplicates(shortNames.stream()).findAny().isPresent()) {
          throw new DuplicateCoordinateConfigurationException(
              Streams.duplicates(shortNames.stream()).findFirst().orElseThrow());
        }

        List<LongSwitchNameCoordinate> longNames = command.getParameters().stream()
            .flatMap(p -> p.getCoordinates().stream())
            .mapMulti(Streams.filterAndCast(LongSwitchNameCoordinate.class)).toList();
        if (Streams.duplicates(longNames.stream()).findAny().isPresent()) {
          throw new DuplicateCoordinateConfigurationException(
              Streams.duplicates(longNames.stream()).findFirst().orElseThrow());
        }
      }
    });
  }
}
