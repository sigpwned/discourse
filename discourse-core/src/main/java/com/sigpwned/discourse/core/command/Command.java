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
package com.sigpwned.discourse.core.command;

import com.sigpwned.discourse.core.ConfigurableClass;
import com.sigpwned.discourse.core.ConfigurationException;
import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.annotation.Configurable;
import com.sigpwned.discourse.core.exception.configuration.NotConfigurableConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.UnexpectedDiscriminatorConfigurationException;
import com.sigpwned.discourse.core.util.Discourse;
import com.sigpwned.discourse.core.util.Discriminators;

/**
 * <p>
 * A command is a blueprint for creating a configurable object from command line arguments and
 * guiding users to provide the right command line arguments. Specifically, it captures the type of
 * configuration class to create, the name of the command, the description of the command, the
 * version of the command, and any command line or environmental parameters needed to build the
 * configuration object.
 * </p>
 *
 * <p>
 * A command can be a {@link SingleCommand single} or {@link MultiCommand multi} command. A single
 * command is a simple, standalone command that simply takes arguments. A multi command is a command
 * that can run any one of multiple subcommands based on a discriminator. This mode is modeled after
 * the AWS CLI and Git CLI.
 * </p>
 *
 * @param <T> The type of the root configuration class.
 */
public abstract sealed class Command<T> permits SingleCommand, MultiCommand {

  /**
   * Scans the given root configuration class with the default context to create a command.
   *
   * @param rawType The root class to scan.
   * @param <T>     The type of the root class.
   * @return The command.
   * @throws ConfigurationException If there is  configuration error on the command
   * @see #scan(InvocationContext, Class)
   * @see Discourse#defaultInvocationContext()
   */
  public static <T> Command<T> scan(Class<T> rawType) {
    return scan(Discourse.defaultInvocationContext(), rawType);
  }

  /**
   * Scans the given root configuration class to create a command.
   *
   * @param context The context
   * @param rawType The root class to scan.
   * @param <T>     The type of the root class.
   * @return The command.
   * @throws ConfigurationException If there is  configuration error on the command
   */
  public static <T> Command<T> scan(InvocationContext context, Class<T> rawType) {
    Configurable configurable = rawType.getAnnotation(Configurable.class);
    if (configurable == null) {
      throw new NotConfigurableConfigurationException(rawType);
    }

    if (Discriminators.fromConfigurable(configurable).isPresent()) {
      throw new UnexpectedDiscriminatorConfigurationException(rawType);
    }

    return subscan(context, ConfigurableClass.scan(rawType));
  }

  /**
   * Scans the given configuration class for a command. This method is for internal use only.
   *
   * @param context           The context
   * @param configurableClass The configuration class to scan.
   * @return The command.
   * @throws ConfigurationException If there is  configuration error on the command
   */
  protected static <T> Command<T> subscan(InvocationContext context,
      ConfigurableClass<T> configurableClass) {
    if (configurableClass.getSubcommands().isEmpty()) {
      // This is a single command.
      return SingleCommand.scan(context, configurableClass);
    } else {
      // This is a multi command.
      return MultiCommand.scan(context, configurableClass);
    }
  }

  private final String name;
  private final String description;
  private final String version;

  /**
   * Creates a new command.
   *
   * @param name        The name of the command.
   * @param description The description of the command.
   * @param version     The version of the command.
   */
  public Command(String name, String description, String version) {
    this.name = name;
    this.description = description;
    this.version = version;
  }

  public abstract Class<T> getRawType();

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public String getVersion() {
    return version;
  }
}
