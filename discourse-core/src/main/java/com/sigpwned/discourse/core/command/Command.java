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

import static java.util.Arrays.asList;

import com.sigpwned.discourse.core.ConfigurationClass;
import com.sigpwned.discourse.core.ConfigurationException;
import com.sigpwned.discourse.core.Invocation;
import com.sigpwned.discourse.core.SerializationContext;
import com.sigpwned.discourse.core.SinkContext;
import com.sigpwned.discourse.core.exception.configuration.UnexpectedDiscriminatorConfigurationException;
import com.sigpwned.discourse.core.parameter.ConfigurationParameter;
import java.util.List;
import java.util.Set;

/**
 * A command that can be populated by environment variables, system properties, and command line
 * arguments.
 */
public abstract sealed class Command<T> permits SingleCommand, MultiCommand {

  /**
   * Scans the given root class for a command.
   *
   * @param storage       The storage context.
   * @param serialization The serialization context.
   * @param rawType       The root class to scan.
   * @param <T>           The type of the root class.
   * @return The command.
   * @throws ConfigurationException If there is  configuration error on the command
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  public static <T> Command<T> scan(SinkContext storage, SerializationContext serialization,
      Class<T> rawType) {
    ConfigurationClass configurableClass = ConfigurationClass.scan(storage, serialization, rawType);

    if (configurableClass.getDiscriminator().isPresent()) {
      throw new UnexpectedDiscriminatorConfigurationException(rawType);
    }

    return (Command) subscan(storage, serialization, configurableClass);
  }

  /**
   * Scans the given configuration class for a command. This method is for internal use only.
   *
   * @param storage            The storage context.
   * @param serialization      The serialization context.
   * @param configurationClass The class to scan.
   * @return The command.
   * @throws ConfigurationException If there is  configuration error on the command
   */
  /* default */
  static Command<?> subscan(SinkContext storage, SerializationContext serialization,
      ConfigurationClass configurationClass) {
    if (configurationClass.getSubcommands().isEmpty()) {
      // This is a single command.
      return SingleCommand.scan(storage, serialization, configurationClass);
    } else {
      // This is a multi command.
      return MultiCommand.scan(storage, serialization, configurationClass);
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

  /**
   * Returns the name for this command, suitable for printing in a help message. May be null.
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the description for this command, suitable for printing in a help message. May be
   * null.
   */
  public String getDescription() {
    return description;
  }

  /**
   * Returns the version for this command, suitable for printing in a version message. May be null.
   */
  public String getVersion() {
    return version;
  }

  /**
   * Returns all unique parameters from this command and any subcommands
   */
  public abstract Set<ConfigurationParameter> getParameters();

  public Invocation<? extends T> args(String... args) {
    return args(asList(args));
  }

  public abstract Invocation<? extends T> args(List<String> args);
}
