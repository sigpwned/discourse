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
import com.sigpwned.discourse.core.annotation.FlagParameter;
import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.command.CommandWalker;
import com.sigpwned.discourse.core.command.SingleCommand;
import com.sigpwned.discourse.core.error.exit.DefaultExitErrorFactory;
import com.sigpwned.discourse.core.format.help.DefaultHelpFormatter;
import com.sigpwned.discourse.core.format.help.HelpFormatter;
import com.sigpwned.discourse.core.model.command.Discriminator;
import com.sigpwned.discourse.core.parameter.FlagConfigurationParameter;
import com.sigpwned.discourse.core.util.Streams;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

/**
 * A {@link DiscourseListener} that looks for the presence of a
 * {@link FlagParameter#help() help flag} in the arguments, and prints the help message and exits if
 * it is found. This interceptor runs during the
 * {@link DiscourseListener#beforeResolve(Command, List, InvocationContext) beforeResolve} event.
 */
public class HelpFlagInterceptingDiscourseListener implements DiscourseListener {

  public static final HelpFlagInterceptingDiscourseListener INSTANCE = new HelpFlagInterceptingDiscourseListener();

  @Override
  public <T> void beforeResolve(Command<T> rootCommand, List<String> args,
      InvocationContext context) {
    Set<String> coordinates = new HashSet<>();
    new CommandWalker().walk(rootCommand, new CommandWalker.Visitor<>() {
      @Override
      public void singleCommand(Discriminator discriminator, SingleCommand<? extends T> command) {
        command.getParameters().stream()
            .mapMulti(Streams.filterAndCast(FlagConfigurationParameter.class))
            .filter(FlagConfigurationParameter::isHelp).flatMap(flag -> Stream.concat(
                Optional.ofNullable(flag.getShortName()).map(Objects::toString).stream(),
                Optional.ofNullable(flag.getLongName()).map(Objects::toString).stream()))
            .forEach(coordinates::add);
      }
    });

    if (coordinates.stream().anyMatch(args::contains)) {
      HelpFormatter formatter = context.get(InvocationContext.HELP_FORMATTER_KEY)
          .orElse(DefaultHelpFormatter.INSTANCE);
      PrintStream err = context.get(InvocationContext.ERROR_STREAM_KEY).orElse(System.err);
      err.print(formatter.formatHelp(rootCommand));
      err.flush();
      throw context.get(InvocationContext.EXIT_ERROR_FACTORY_KEY)
          .orElse(DefaultExitErrorFactory.INSTANCE).createExitError(0);
    }
  }
}
