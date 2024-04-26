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
import com.sigpwned.discourse.core.Invocation;
import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.InvocationStrategy;
import com.sigpwned.discourse.core.format.version.VersionFormatter;
import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.command.SingleCommand;
import com.sigpwned.discourse.core.format.version.DefaultVersionFormatter;
import com.sigpwned.discourse.core.parameter.FlagConfigurationParameter;
import com.sigpwned.discourse.core.util.Args;
import com.sigpwned.discourse.core.util.Streams;
import java.io.PrintStream;
import java.util.List;

/**
 * An {@link InvocationStrategy} that looks for the presence of a version flag in the arguments and
 * prints the help message and exits if it is found. Otherwise, it delegates to another strategy.
 */
public class VersionPrintingInvocationStrategy implements InvocationStrategy {

  private static final VersionFormatter DEFAULT_FORMATTER = DefaultVersionFormatter.INSTANCE;

  private static final PrintStream DEFAULT_ERROR_STREAM = System.err;

  private final InvocationStrategy delegate;

  public VersionPrintingInvocationStrategy(InvocationStrategy delegate) {
    this.delegate = requireNonNull(delegate);
  }

  @Override
  public <T> Invocation<? extends T> invoke(Command<T> command, InvocationContext context,
      List<String> args) {
    if (!(command instanceof SingleCommand<T> single)) {
      throw new IllegalArgumentException("Command is not a SingleCommand");
    }

    single.getParameters().stream()
        .mapMulti(Streams.filterAndCast(FlagConfigurationParameter.class))
        .filter(FlagConfigurationParameter::isVersion).findFirst().ifPresent(versionFlag -> {
          if (Args.containsFlag(args, versionFlag.getShortName(), versionFlag.getLongName())) {
            VersionFormatter formatter = getVersionFormatter(context);
            PrintStream err = getErrorStream(context);
            err.print(formatter.formatVersion(command));
            err.flush();
            throw exit(0);
          }
        });

    return getDelegate().invoke(command, context, args);
  }

  private InvocationStrategy getDelegate() {
    return delegate;
  }

  private VersionFormatter getVersionFormatter(InvocationContext context) {
    return context.get(InvocationContext.VERSION_FORMATTER_KEY).orElse(DEFAULT_FORMATTER);
  }

  private PrintStream getErrorStream(InvocationContext context) {
    return context.get(InvocationContext.ERROR_STREAM_KEY).orElse(DEFAULT_ERROR_STREAM);
  }

  /**
   * test hook
   */
  protected ExitError exit(int status) {
    System.exit(status);
    return new ExitError(status);
  }
}
