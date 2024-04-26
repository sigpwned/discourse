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
package com.sigpwned.discourse.core.invocation.strategy;

import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.util.error.ExitError;
import com.sigpwned.discourse.core.format.help.HelpFormatter;
import com.sigpwned.discourse.core.Invocation;
import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.InvocationStrategy;
import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.command.MultiCommand;
import com.sigpwned.discourse.core.command.SingleCommand;
import com.sigpwned.discourse.core.exception.syntax.RequiredParametersMissingSyntaxException;
import com.sigpwned.discourse.core.exception.syntax.UnrecognizedDiscriminatorSyntaxException;
import com.sigpwned.discourse.core.format.help.DefaultHelpFormatter;
import java.io.PrintStream;
import java.util.List;

/**
 * <p>
 * An {@link InvocationStrategy} that detects common situations where the user is likely asking for
 * help and prints the help message and exits if necessary. Otherwise, it delegates to another
 * strategy. The situations where the help message is printed are as follows:
 * </p>
 *
 * <ol>
 *   <li>The user provides only the argument "help" and no other arguments to a single command</li>
 *   <li>The user provides no arguments to a multi command</li>
 *   <li>
 *     The user doesn't provide all the required parameters to a single command and the given args
 *     are empty
 *   </li>
 *   <li>
 *     The user provides the discriminator "help" to a multi command and the command doesn't handle
 *     the "help" discriminator
 *   </li>
 * </ol>
 *
 * <p>
 * Any explicit help flags are handled by the {@link HelpPrintingInvocationStrategy} and not this
 * strategy.
 * </p>
 */
public class AutoHelpInvocationStrategy implements InvocationStrategy {

  private static final HelpFormatter DEFAULT_FORMATTER = DefaultHelpFormatter.INSTANCE;

  private static final PrintStream DEFAULT_ERROR_STREAM = System.err;

  public static final String HELP = "help";

  private final InvocationStrategy delegate;

  public AutoHelpInvocationStrategy(InvocationStrategy delegate) {
    this.delegate = requireNonNull(delegate);
  }

  @Override
  public <T> Invocation<? extends T> invoke(Command<T> command, InvocationContext context,
      List<String> args) {
    if (command instanceof SingleCommand<T> single) {
      if (args.equals(List.of(HELP))) {
        // If the user provided only the argument "help" and no other arguments to a single command,
        // then print the help message and exit.
        printHelp(context, single);
        throw exit(0);
      } else {
        try {
          return getDelegate().invoke(command, context, args);
        } catch (RequiredParametersMissingSyntaxException e) {
          // If the user didn't provide all the required parameters and the given args are empty,
          // then print the help message and exit. Otherwise, rethrow the exception. Note that we
          // don't print the help message if the user provided no arguments, but the command has no
          // required parameters.
          if (args.isEmpty()) {
            printHelp(context, single);
            throw exit(0);
          } else {
            throw e;
          }
        }
      }
    } else if (command instanceof MultiCommand<T> multi) {
      if (args.isEmpty()) {
        // If the user provided no arguments to a multi command, then print the help message and
        // exit. We know this is not valid input because a multi command requires a subcommand.
        printHelp(context, multi);
        throw exit(0);
      }
      try {
        return getDelegate().invoke(multi, context, args);
      } catch (UnrecognizedDiscriminatorSyntaxException e) {
        // If the user provided the discriminator "help" to a multi command and it is unrecognized,
        // then print the help message and exit. Otherwise, rethrow the exception. Note that we
        // don't print the help message if the help discriminator is recognized.
        if (e.getDiscriminator().toString().equals(HELP)) {
          // Note that we use the command from the exception, not the command passed to this method.
          // We want to print explicit help for the subcommand where help was requested, not the
          // upstream multi command.
          printHelp(context, e.getCommand());
          throw exit(0);
        } else {
          throw e;
        }
      }
    }
    throw new AssertionError("unrecognized command: " + command);
  }

  private InvocationStrategy getDelegate() {
    return delegate;
  }

  private HelpFormatter getHelpFormatter(InvocationContext context) {
    return context.get(InvocationContext.HELP_FORMATTER_KEY).orElse(DEFAULT_FORMATTER);
  }

  private PrintStream getErrorStream(InvocationContext context) {
    return context.get(InvocationContext.ERROR_STREAM_KEY).orElse(DEFAULT_ERROR_STREAM);
  }

  private void printHelp(InvocationContext context, Command<?> command) {
    PrintStream err = getErrorStream(context);
    HelpFormatter formatter = getHelpFormatter(context);
    err.print(formatter.formatHelp(command));
    err.flush();
  }

  /**
   * test hook
   */
  protected ExitError exit(int status) {
    System.exit(status);
    return new ExitError(status);
  }
}
