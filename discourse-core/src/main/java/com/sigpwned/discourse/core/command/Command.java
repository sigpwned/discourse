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
import com.sigpwned.discourse.core.SerializationContext;
import com.sigpwned.discourse.core.SinkContext;
import com.sigpwned.discourse.core.annotation.Configurable;
import com.sigpwned.discourse.core.exception.configuration.NotConfigurableConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.UnexpectedDiscriminatorConfigurationException;
import com.sigpwned.discourse.core.util.Discriminators;

public abstract sealed class Command<T> permits SingleCommand, MultiCommand {

  /**
   * Scans the given root configuration class to create a command.
   *
   * @param storage       The storage context.
   * @param serialization The serialization context.
   * @param rawType       The root class to scan.
   * @param <T>           The type of the root class.
   * @return The command.
   * @throws ConfigurationException If there is  configuration error on the command
   */
  public static <T> Command<T> scan(SinkContext storage, SerializationContext serialization,
      Class<T> rawType) {
    Configurable configurable = rawType.getAnnotation(Configurable.class);
    if (configurable == null) {
      throw new NotConfigurableConfigurationException(rawType);
    }

    if (Discriminators.fromConfigurable(configurable).isPresent()) {
      throw new UnexpectedDiscriminatorConfigurationException(rawType);
    }

    return subscan(storage, serialization, ConfigurableClass.scan(rawType));
  }

  /**
   * Scans the given configuration class for a command. This method is for internal use only.
   *
   * @param storage           The storage context.
   * @param serialization     The serialization context.
   * @param configurableClass The configuration class to scan.
   * @return The command.
   * @throws ConfigurationException If there is  configuration error on the command
   */
  protected static <T> Command<T> subscan(SinkContext storage, SerializationContext serialization,
      ConfigurableClass<T> configurableClass) {
    if (configurableClass.getSubcommands().isEmpty()) {
      // This is a single command.
      return SingleCommand.scan(storage, serialization, configurableClass);
    } else {
      // This is a multi command.
      return MultiCommand.scan(storage, serialization, configurableClass);
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
