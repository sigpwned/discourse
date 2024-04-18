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

import static java.lang.String.*;
import static java.util.Collections.*;

import com.sigpwned.discourse.core.Command;
import com.sigpwned.discourse.core.ConfigurableClass;
import com.sigpwned.discourse.core.ConfigurableClass.SubcommandClass;
import com.sigpwned.discourse.core.Discriminator;
import com.sigpwned.discourse.core.SerializationContext;
import com.sigpwned.discourse.core.SinkContext;
import com.sigpwned.discourse.core.exception.configuration.DiscriminatorMismatchConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.MultiCommandNotAbstractConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.NoDiscriminatorConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.SubcommandDoesNotExtendRootCommandConfigurationException;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class MultiCommand<T> extends Command<T> {

  public static <T> MultiCommand<T> scan(SinkContext storage, SerializationContext serialization,
      ConfigurableClass<T> configurableClass) {
    if (configurableClass.getSubcommands().isEmpty()) {
      // TODO This should be a configuration exception
      throw new IllegalArgumentException(
          format("Configurable %s has no subcommands", configurableClass.getRawType().getName()));
    }
    if (!Modifier.isAbstract(configurableClass.getRawType().getModifiers())) {
      throw new MultiCommandNotAbstractConfigurationException(configurableClass.getRawType());
    }

    Map<Discriminator, Command<? extends T>> subcommands = new LinkedHashMap<>();
    for (SubcommandClass<? extends T> subcommand : configurableClass.getSubcommands()) {
      ConfigurableClass<? extends T> subcommandClass = ConfigurableClass.scan(subcommand.rawType());

      Discriminator subcommandDiscriminator = subcommandClass.getDiscriminator().orElseThrow(
          () -> new NoDiscriminatorConfigurationException(subcommandClass.getRawType()));

      if (subcommand.discriminator().isPresent() && !subcommand.discriminator().orElseThrow()
          .equals(subcommandDiscriminator)) {
        throw new DiscriminatorMismatchConfigurationException(subcommandClass.getRawType(),
            subcommandDiscriminator, subcommand.discriminator().orElseThrow());
      }

      if (!Objects.equals(subcommandClass.getRawType().getSuperclass(),
          configurableClass.getRawType())) {
        throw new SubcommandDoesNotExtendRootCommandConfigurationException(
            configurableClass.getRawType(), subcommandClass.getRawType());
      }

      subcommands.put(subcommandDiscriminator,
          Command.subscan(storage, serialization, subcommandClass));
    }

    return new MultiCommand<>(configurableClass.getName(), configurableClass.getDescription(),
        configurableClass.getVersion(), subcommands);
  }

  private final Map<Discriminator, Command<? extends T>> subcommands;

  public MultiCommand(String name, String description, String version,
      Map<Discriminator, Command<? extends T>> subcommands) {
    super(name, description, version);
    if (subcommands.isEmpty()) {
      throw new IllegalArgumentException("no subcommands");
    }
    this.subcommands = unmodifiableMap(subcommands);
  }

  /**
   * @return the subcommands
   */
  public Map<Discriminator, Command<? extends T>> getSubcommands() {
    return subcommands;
  }
}
