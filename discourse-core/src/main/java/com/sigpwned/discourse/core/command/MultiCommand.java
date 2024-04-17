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
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toSet;

import com.sigpwned.discourse.core.ConfigurationClass;
import com.sigpwned.discourse.core.ConfigurationClass.SubcommandClass;
import com.sigpwned.discourse.core.Discriminator;
import com.sigpwned.discourse.core.Invocation;
import com.sigpwned.discourse.core.SerializationContext;
import com.sigpwned.discourse.core.SinkContext;
import com.sigpwned.discourse.core.exception.argument.InvalidDiscriminatorArgumentException;
import com.sigpwned.discourse.core.exception.argument.NoSubcommandArgumentException;
import com.sigpwned.discourse.core.exception.argument.UnrecognizedSubcommandArgumentException;
import com.sigpwned.discourse.core.exception.configuration.DiscriminatorMismatchConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.MultiCommandNotAbstractConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.NoDiscriminatorConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.SubcommandDoesNotExtendRootCommandConfigurationException;
import com.sigpwned.discourse.core.parameter.ConfigurationParameter;
import com.sigpwned.discourse.core.parameter.FlagConfigurationParameter;
import com.sigpwned.discourse.core.parameter.OptionConfigurationParameter;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public final class MultiCommand<T> extends Command<T> {

  public static <T> MultiCommand<T> scan(SinkContext storage, SerializationContext serialization,
      ConfigurationClass<T> configurationClass) {
    if (configurationClass.getSubcommands().isEmpty()) {
      // TODO This should be a configuration exception
      throw new IllegalArgumentException(
          format("Configurable %s has no subcommands", configurationClass.getRawType().getName()));
    }
    if (!Modifier.isAbstract(configurationClass.getRawType().getModifiers())) {
      throw new MultiCommandNotAbstractConfigurationException(configurationClass.getRawType());
    }

    Map<Discriminator, Command<? extends T>> subcommands = new LinkedHashMap<>();
    for (SubcommandClass<? extends T> subcommand : configurationClass.getSubcommands()) {
      ConfigurationClass<? extends T> subcommandClass = ConfigurationClass.scan(
          subcommand.rawType());

      Discriminator subcommandDiscriminator = subcommandClass.getDiscriminator().orElseThrow(
          () -> new NoDiscriminatorConfigurationException(subcommandClass.getRawType()));

      if (subcommand.discriminator().isPresent() && !subcommand.discriminator().orElseThrow()
          .equals(subcommandDiscriminator)) {
        throw new DiscriminatorMismatchConfigurationException(subcommandClass.getRawType(),
            subcommandDiscriminator, subcommand.discriminator().orElseThrow());
      }

      if (!Objects.equals(subcommandClass.getRawType().getSuperclass(),
          configurationClass.getRawType())) {
        throw new SubcommandDoesNotExtendRootCommandConfigurationException(
            configurationClass.getRawType(), subcommandClass.getRawType());
      }

      subcommands.put(subcommandDiscriminator,
          Command.subscan(storage, serialization, subcommandClass));
    }

    return new MultiCommand<>(configurationClass.getName(), configurationClass.getDescription(),
        configurationClass.getVersion(), subcommands);
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

  public Set<Discriminator> listSubcommands() {
    return getSubcommands().keySet();
  }

  public Optional<Command<? extends T>> getSubcommand(Discriminator discriminator) {
    return Optional.ofNullable(getSubcommands().get(discriminator));
  }

  /**
   * Returns all unique parameters from any subcommand
   */
  @Override
  public Set<ConfigurationParameter> getParameters() {
    return getSubcommands().values().stream().flatMap(c -> c.getParameters().stream())
        .collect(toSet());
  }

  /**
   * Returns all option and flag parameters shared by all subcommands
   */
  public Set<ConfigurationParameter> getCommonParameters() {
    return getSubcommands().values().stream().flatMap(c -> c.getParameters().stream()).filter(
            p -> p instanceof OptionConfigurationParameter || p instanceof FlagConfigurationParameter)
        .collect(groupingBy(p -> p, counting())).entrySet().stream()
        .filter(e -> e.getValue() == getSubcommands().size()).map(Map.Entry::getKey)
        .collect(toSet());
  }

  /**
   * @return the subcommands
   */
  private Map<Discriminator, Command<? extends T>> getSubcommands() {
    return subcommands;
  }

  @Override
  public Invocation<? extends T> args(List<String> args) {
    if (args.isEmpty()) {
      throw new NoSubcommandArgumentException();
    }

    Discriminator discriminator;
    try {
      discriminator = Discriminator.fromString(args.get(0));
    } catch (IllegalArgumentException e) {
      throw new InvalidDiscriminatorArgumentException(args.get(0));
    }

    Command<? extends T> subcommand = getSubcommand(discriminator).orElseThrow(
        () -> new UnrecognizedSubcommandArgumentException(discriminator));

    return subcommand.args(args.subList(1, args.size()));
  }
}
