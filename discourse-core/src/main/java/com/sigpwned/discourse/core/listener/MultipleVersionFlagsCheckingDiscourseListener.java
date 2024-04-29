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
import com.sigpwned.discourse.core.exception.configuration.MultipleVersionFlagsConfigurationException;
import com.sigpwned.discourse.core.model.command.Discriminator;
import com.sigpwned.discourse.core.parameter.FlagConfigurationParameter;
import com.sigpwned.discourse.core.util.Streams;

public class MultipleVersionFlagsCheckingDiscourseListener implements DiscourseListener {

  public static final MultipleVersionFlagsCheckingDiscourseListener INSTANCE = new MultipleVersionFlagsCheckingDiscourseListener();

  @Override
  public <T> void afterScan(Command<T> rootCommand, InvocationContext context) {
    checkForMultipleVersionFlags(rootCommand);
  }

  /**
   * Check for multiple version flags in the given command. We break this out into a separate method
   * so that it can be overridden by subclasses without worrying about where it is called in the
   * listener lifecycle.
   */
  protected <T> void checkForMultipleVersionFlags(Command<T> command) {
    new CommandWalker().walk(command, new CommandWalker.Visitor<T>() {
      @Override
      public void singleCommand(Discriminator discriminator, SingleCommand<? extends T> command) {
        if (command.getParameters().stream()
            .mapMulti(Streams.filterAndCast(FlagConfigurationParameter.class))
            .filter(FlagConfigurationParameter::isVersion).count() > 1) {
          throw new MultipleVersionFlagsConfigurationException(command.getRawType());
        }
      }
    });
  }
}
