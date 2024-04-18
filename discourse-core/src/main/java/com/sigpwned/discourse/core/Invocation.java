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

import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.command.MultiCommand;
import com.sigpwned.discourse.core.command.SingleCommand;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * <p>
 * Represents a {@link Command command} invocation. This is the result of parsing the command line
 * arguments and using them to create a configuration object.
 * </p>
 *
 * <p>
 * Note that the leaf commandO is of type {@link SingleCommand}. In the case where the "root"
 * command being executed is a {@link MultiCommand}, the {@link InvocationStrategy} is responsible
 * for dereferencing the subcommands and invoking the underlying "leaf" {@code SingleCommand}.
 * </p>
 *
 * <p>
 * The {@link #getLeafArgs() leaf args} are the arguments that were passed to the command. In cases
 * where the command has been dereferenced, the args will NOT contain the "discriminator" arguments
 * that were used to dereference the subcommands to find the "leaf" {@code SingleCommand}. The
 * verbatim command line arguments passed to the application are available as
 * {@link #getAllArgs() all args}.
 * </p>
 *
 * <p>
 * The discriminators and the {@code MultiCommand}s they map to are available via
 * {@link #getSubcommands()}.
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
public interface Invocation<T> {

  /**
   * The configuration object created from the command line arguments according to the command being
   * executed.
   *
   * @return the configuration
   */
  T getConfiguration();

  /**
   * The discriminators and the {@link MultiCommand}s they map to. If the root command is of type
   * {@link SingleCommand}, then this will be empty. Otherwise, this will contain the discriminators
   * that were used to dereference the subcommands to find the "leaf" {@code SingleCommand}, and the
   * {@code MultiCommand}s that were dereferenced.
   *
   * @return the subcommands
   */
  List<Map.Entry<Discriminator, MultiCommand<? extends T>>> getSubcommands();

  /**
   * The "leaf" {@link SingleCommand} that was invoked. If the root command is of type
   * {@code SingleCommand}, then this will be the same as the root command. Otherwise, this will be
   * the "leaf" {@code SingleCommand} that was dereferenced.
   *
   * @return the command
   */
  SingleCommand<? extends T> getLeafCommand();

  /**
   * The arguments that were passed to the command. If the root command is of type
   * {@code SingleCommand}, then this will be the same as the arguments that were passed to the
   * application. Otherwise, this will be the arguments that were passed to the "leaf"
   * {@code SingleCommand}. The prefix "discriminator" arguments that were used to dereference the
   * subcommands to find the "leaf" {@code SingleCommand} will NOT be included in this list. (These
   * discriminator arguments can be found in {@link #getSubcommands()}, in the order they originally
   * appeared in the application arguments.)
   *
   * @return the args
   */
  List<String> getLeafArgs();

  /**
   * The verbatim arguments as they were passed to the application. This includes the
   * "discriminator" arguments that were used to dereference the subcommands to find the "leaf"
   * {@code SingleCommand} (if the root command is of type {@code MultiCommand}), and the arguments
   * that were passed to the "leaf" {@code SingleCommand}.
   *
   * @return the verbatim arguments
   */
  default List<String> getAllArgs() {
    return Stream.concat(getSubcommands().stream().map(Map.Entry::getKey).map(Objects::toString),
        getLeafArgs().stream()).toList();
  }
}
