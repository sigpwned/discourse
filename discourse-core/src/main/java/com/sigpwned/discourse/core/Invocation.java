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
package com.sigpwned.discourse.core;

import static java.util.Collections.*;
import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.command.MultiCommand;
import com.sigpwned.discourse.core.command.SingleCommand;
import com.sigpwned.discourse.core.invocation.InvocationBuilder;
import com.sigpwned.discourse.core.model.invocation.MultiCommandDereference;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * Represents a {@link Command command} invocation. This is the result of parsing the command line
 * arguments and using them to create a configuration object. No methods are invoked on the
 * configuration object itself after creation.
 * </p>
 *
 * <p>
 * Note that the leaf command is of type {@link SingleCommand}. In the case where the "root" command
 * being executed is a {@link MultiCommand}, the {@link InvocationStrategy} is responsible for
 * dereferencing the subcommands and invoking the underlying "leaf" {@code SingleCommand}.
 * </p>
 *
 * <p>
 * The discriminators and the {@code MultiCommand}s they map to are available via
 * {@link #getDereferencedCommands()}.
 * </p>
 *
 * <p>
 * The configuration object constructed from the arguments is available via
 * {@link #getConfiguration()}.
 * </p>
 *
 * <p>
 * The environment variables are available via {@link System#getenv()}, and the system properties
 * are available via {@link System#getProperties()}.
 * </p>
 *
 * @param <T> The type of the configuration object.
 */
public class Invocation<T> {

  /**
   * Create a new {@link Invocation} builder using the default implementation.
   *
   * @return the builder
   */
  public static InvocationBuilder builder() {
    return new InvocationBuilder();
  }

  private final Command<T> rootCommand;
  private final List<MultiCommandDereference<? extends T>> dereferencedCommands;
  private final SingleCommand<? extends T> resolvedCommand;
  private final T configuration;

  public Invocation(Command<T> rootCommand,
      List<MultiCommandDereference<? extends T>> dereferencedCommands,
      SingleCommand<? extends T> resolvedCommand, T configuration) {
    this.rootCommand = requireNonNull(rootCommand);
    this.dereferencedCommands = unmodifiableList(dereferencedCommands);
    this.resolvedCommand = requireNonNull(resolvedCommand);
    this.configuration = requireNonNull(configuration);
  }

  public Command<T> getRootCommand() {
    return rootCommand;
  }

  public List<MultiCommandDereference<? extends T>> getDereferencedCommands() {
    return dereferencedCommands;
  }

  public SingleCommand<? extends T> getResolvedCommand() {
    return resolvedCommand;
  }

  public T getConfiguration() {
    return configuration;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Invocation<?> that)) {
      return false;
    }
    return Objects.equals(getRootCommand(), that.getRootCommand()) && Objects.equals(
        getDereferencedCommands(), that.getDereferencedCommands()) && Objects.equals(
        getResolvedCommand(), that.getResolvedCommand()) && Objects.equals(getConfiguration(),
        that.getConfiguration());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getRootCommand(), getDereferencedCommands(), getResolvedCommand(),
        getConfiguration());
  }

  @Override
  public String toString() {
    return "Invocation{" + "rootCommand=" + rootCommand + ", dereferencedCommands="
        + dereferencedCommands + ", resolvedCommand=" + resolvedCommand + ", configuration="
        + configuration + '}';
  }
}
