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

import com.sigpwned.discourse.core.HelpFormatter;
import com.sigpwned.discourse.core.Invocation;
import com.sigpwned.discourse.core.InvocationStrategy;
import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.command.SingleCommand;
import com.sigpwned.discourse.core.format.help.DefaultHelpFormatter;
import com.sigpwned.discourse.core.parameter.FlagConfigurationParameter;
import com.sigpwned.discourse.core.util.Args;
import com.sigpwned.discourse.core.util.Streams;
import java.io.PrintStream;
import java.util.List;

/**
 * An {@link InvocationStrategy} that looks for the presence of a help flag in the arguments and
 * prints the help message and exits if it is found. Otherwise, it delegates to another strategy.
 */
public class HelpPrintingInvocationStrategy implements InvocationStrategy {

  public static final HelpFormatter DEFAULT_FORMATTER = DefaultHelpFormatter.INSTANCE;

  public static final PrintStream DEFAULT_OUTPUT = System.err;

  private final InvocationStrategy delegate;
  private final HelpFormatter formatter;
  private final PrintStream output;

  public HelpPrintingInvocationStrategy(InvocationStrategy delegate) {
    this(delegate, DEFAULT_FORMATTER);
  }

  public HelpPrintingInvocationStrategy(InvocationStrategy delegate, HelpFormatter formatter) {
    this(delegate, formatter, DEFAULT_OUTPUT);
  }

  public HelpPrintingInvocationStrategy(InvocationStrategy delegate, HelpFormatter formatter,
      PrintStream output) {
    this.delegate = requireNonNull(delegate);
    this.formatter = requireNonNull(formatter);
    this.output = requireNonNull(output);
  }

  @Override
  public <T> Invocation<? extends T> invoke(Command<T> command, List<String> args) {
    if (!(command instanceof SingleCommand<T> single)) {
      throw new IllegalArgumentException("Command is not a SingleCommand");
    }

    single.getParameters().stream()
        .mapMulti(Streams.filterAndCast(FlagConfigurationParameter.class))
        .filter(FlagConfigurationParameter::isHelp).findFirst().ifPresent(helpFlag -> {
          if (Args.containsFlag(args, helpFlag.getShortName(), helpFlag.getLongName())) {
            getOutput().println(getFormatter().formatHelp(single));
            System.exit(0);
          }
        });

    return getDelegate().invoke(command, args);
  }

  private InvocationStrategy getDelegate() {
    return delegate;
  }

  private HelpFormatter getFormatter() {
    return formatter;
  }

  private PrintStream getOutput() {
    return output;
  }
}
