/*-
 * =================================LICENSE_START==================================
 * discourse-core
 * ====================================SECTION=====================================
 * Copyright (C) 2022 Andy Boothe
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
package com.sigpwned.discourse.core.util;

import static java.util.Arrays.asList;

import com.sigpwned.discourse.core.ArgumentException;
import com.sigpwned.discourse.core.ConfigurationException;
import com.sigpwned.discourse.core.HelpFormatter;
import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.InvocationStrategy;
import com.sigpwned.discourse.core.SyntaxException;
import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.format.help.DefaultHelpFormatter;
import com.sigpwned.discourse.core.invocation.context.DefaultInvocationContext;
import com.sigpwned.discourse.core.invocation.strategy.DefaultInvocationStrategy;
import com.sigpwned.discourse.core.module.DefaultModule;
import java.io.PrintStream;
import java.util.List;

public final class Discourse {

  private Discourse() {
  }

  /**
   * Creates a default invocation strategy.
   */
  public static InvocationStrategy defaultInvocationStrategy() {
    return new DefaultInvocationStrategy();
  }

  /**
   * Creates a default invocation context.
   */
  public static InvocationContext defaultInvocationContext() {
    return DefaultInvocationContext.builder().register(new DefaultModule()).build();
  }

  /**
   * Create a configuration object of the given type from the given arguments.
   */
  public static <T> T configuration(Class<T> rawType, String[] args) {
    return configuration(rawType, asList(args));
  }

  /**
   * Create a configuration object of the given type from the given arguments using the given
   * command builder.
   */
  public static <T> T configuration(Class<T> rawType, InvocationStrategy invoker,
      InvocationContext context, String[] args) {
    return configuration(rawType, invoker, context, List.of(args));
  }

  /**
   * Create a configuration object of the given type from the given arguments.
   */
  public static <T> T configuration(Class<T> rawType, List<String> args) {
    return configuration(rawType, defaultInvocationStrategy(), defaultInvocationContext(), args);
  }

  /**
   * Create a configuration object of the given type from the given arguments using the given
   * command builder.
   */
  public static <T> T configuration(Class<T> rawType, InvocationStrategy invoker,
      InvocationContext context, List<String> args) {

    Command<T> command;
    try {
      command = Command.scan(context, rawType);
    } catch (ConfigurationException e) {
      PrintStream err = context.<PrintStream>get(InvocationContext.ERROR_STREAM_KEY)
          .orElse(System.err);
      err.println("There was a problem with the application configuration.");
      err.println("You should reach out to the application developer for help.");
      err.println("They may find the following information useful:");
      err.println("ARGUMENTS: " + args);
      err.println("STACK TRACE:");
      e.printStackTrace(err);
      throw exit(1);
    }

    T result;
    try {
      result = invoker.invoke(command, context, args).getConfiguration();
    } catch (SyntaxException e) {
      // In this case, the user has made a mistake in the command line syntax. The command line
      // cannot be understood, so we do our best to figure out what the problem is and print a
      // helpful error message.
      PrintStream err = context.<PrintStream>get(InvocationContext.ERROR_STREAM_KEY)
          .orElse(System.err);
      if (args.isEmpty()) {
        HelpFormatter formatter = context.<HelpFormatter>get(InvocationContext.HELP_FORMATTER_KEY)
            .orElse(DefaultHelpFormatter.INSTANCE);
        err.println(formatter.formatHelp(command));
      } else {
        err.println("ERROR: " + e.getMessage());
      }
      throw exit(2);
    } catch (ArgumentException e) {
      // In this case, the user has made a mistake in the command line arguments. The command line
      // is understood, but the arguments are not valid. We print a helpful error message.
      PrintStream err = context.<PrintStream>get(InvocationContext.ERROR_STREAM_KEY)
          .orElse(System.err);
      err.println("ERROR: " + e.getMessage());
      throw exit(3);
    } catch (RuntimeException e) {
      PrintStream err = context.<PrintStream>get(InvocationContext.ERROR_STREAM_KEY)
          .orElse(System.err);
      err.println("ERROR: " + e.getMessage());
      throw exit(4);
    }

    return result;
  }

  private static AssertionError exit(int code) {
    System.exit(code);
    return new AssertionError("exit");
  }
}
