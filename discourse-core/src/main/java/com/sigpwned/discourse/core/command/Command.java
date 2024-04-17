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

import com.sigpwned.discourse.core.Invocation;
import com.sigpwned.discourse.core.SerializationContext;
import com.sigpwned.discourse.core.SinkContext;
import com.sigpwned.discourse.core.annotation.Configurable;
import com.sigpwned.discourse.core.exception.configuration.UnexpectedDiscriminatorConfigurationException;
import com.sigpwned.discourse.core.parameter.ConfigurationParameter;
import java.util.List;
import java.util.Set;

public abstract sealed class Command<T> permits SingleCommand, MultiCommand, TreeCommand {

  public static <T> Command<T> scan(SinkContext storage, SerializationContext serialization,
      Class<T> rawType) {
    Configurable configurable = rawType.getAnnotation(Configurable.class);

    if (configurable == null) {
      throw new IllegalArgumentException("Root configurable is not configurable");
    }

    if (!configurable.discriminator().isEmpty()) {
      throw new UnexpectedDiscriminatorConfigurationException(rawType);
    }

    if (configurable.subcommands().length == 0) {
      // This is a single command.
      return SingleCommand.scan(storage, serialization, rawType);
    } else {
      // This is a multi command.
      return MultiCommand.scan(storage, serialization, rawType);
    }
  }

  /**
   * Returns the name for this command, suitable for printing in a help message.
   */
  public abstract String getName();

  /**
   * Returns the description for this command, suitable for printing in a help message.
   */
  public abstract String getDescription();

  /**
   * Returns the version for this command, suitable for printing in a version message.
   */
  public abstract String getVersion();

  /**
   * Returns all unique parameters from this command and any subcommands
   */
  public abstract Set<ConfigurationParameter> getParameters();

  public Invocation<T> args(String... args) {
    return args(asList(args));
  }

  public abstract Invocation<T> args(List<String> args);
}
