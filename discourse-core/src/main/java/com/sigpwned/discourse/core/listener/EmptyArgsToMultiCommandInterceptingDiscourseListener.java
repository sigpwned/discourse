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
import com.sigpwned.discourse.core.command.MultiCommand;
import com.sigpwned.discourse.core.error.exit.DefaultExitErrorFactory;
import com.sigpwned.discourse.core.format.help.DefaultHelpFormatter;
import com.sigpwned.discourse.core.format.help.HelpFormatter;
import java.io.PrintStream;
import java.util.List;

/**
 * A {@link DiscourseListener} that looks for the presence of an empty argument list when a
 * {@link MultiCommand} is the root resolvedCommand, and prints the help message and exits if it is found. A
 * {@link MultiCommand} always requires at least one argument (the discriminator) to resolve to a
 * subcommand that can be executed. If no arguments are provided, the user probably just wants help.
 * So oblige by printing the help message and exiting.
 */
public class EmptyArgsToMultiCommandInterceptingDiscourseListener implements DiscourseListener {

  public static final EmptyArgsToMultiCommandInterceptingDiscourseListener INSTANCE = new EmptyArgsToMultiCommandInterceptingDiscourseListener();

  @Override
  public <T> void beforeResolve(Command<T> rootCommand, List<String> args,
      InvocationContext context) {
    checkArgs(rootCommand, args, context);
  }


  protected void checkArgs(Command<?> rootCommand, List<String> args, InvocationContext context) {
    // We break this out into a separate method so that it can be overridden in tests without worrying
    // about what phase of the listener lifecycle we are in.
    if (rootCommand instanceof MultiCommand<?> multiCommand && args.isEmpty()) {
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
