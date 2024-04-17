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
package com.sigpwned.discourse.core;

import static java.util.Collections.*;
import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.annotation.Configurable;
import com.sigpwned.discourse.core.exception.configuration.DuplicateDiscriminatorConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.InvalidDiscriminatorConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.NoDiscriminatorConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.NotConfigurableConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.SealedSubcommandsConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.SubcommandDoesNotExtendRootCommandConfigurationException;
import com.sigpwned.discourse.core.util.Streams;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class ConfigurationClass<T> {

  public static record SubcommandClass<T>(Optional<Discriminator> discriminator, Class<T> rawType) {

    public SubcommandClass {
      discriminator = requireNonNull(discriminator);
      rawType = requireNonNull(rawType);
    }
  }

  public static <T> ConfigurationClass<T> scan(Class<T> rawType) {
    final Configurable configurable = rawType.getAnnotation(Configurable.class);
    if (configurable == null) {
      throw new NotConfigurableConfigurationException(rawType);
    }

    final String name = Optional.of(configurable.name()).filter(s -> !s.isEmpty()).orElse(null);

    final String description = Optional.of(configurable.description()).filter(s -> !s.isEmpty())
        .orElse(null);

    final String version = Optional.of(configurable.version()).filter(s -> !s.isEmpty())
        .orElse(null);

    final List<SubcommandClass<? extends T>> subcommands = subcommands(rawType, configurable);

    validateSubcommands(rawType, subcommands).ifPresent(e -> {
      throw e;
    });

    final Discriminator discriminator;
    try {
      discriminator = Optional.of(configurable.discriminator()).filter(s -> !s.isEmpty())
          .map(Discriminator::fromString).orElse(null);
    } catch (IllegalArgumentException e) {
      throw new InvalidDiscriminatorConfigurationException(rawType, configurable.discriminator());
    }

    return new ConfigurationClass<>(rawType, discriminator, name, description, version,
        subcommands);
  }

  private final Class<T> rawType;
  private final Discriminator discriminator;
  private final String name;
  private final String description;
  private final String version;
  private final List<SubcommandClass<? extends T>> subcommands;

  private ConfigurationClass(Class<T> rawType, Discriminator discriminator, String name,
      String description, String version, List<SubcommandClass<? extends T>> subcommands) {
//    Streams.duplicates(parameters.stream()
//            .map(p -> p.getCoordinates().stream().map(c -> Map.entry(c, p.getName())))).findFirst()
//        .ifPresent(c -> {
//          throw new IllegalArgumentException(format("coordinates defined more than once: %s", c));
//        });
//
//    if (parameters.stream().mapMulti(ConfigurationParameters.mapMultiFlag())
//        .filter(FlagConfigurationParameter::isHelp).count() > 1L) {
//      throw new IllegalArgumentException("multiple help flags");
//    }
//
//    if (parameters.stream().mapMulti(ConfigurationParameters.mapMultiFlag())
//        .filter(FlagConfigurationParameter::isVersion).count() > 1L) {
//      throw new IllegalArgumentException("multiple version flags");
//    }

    if (Streams.duplicates(
            subcommands.stream().map(SubcommandClass::discriminator).flatMap(Optional::stream))
        .findFirst().isPresent()) {
      throw new IllegalArgumentException("duplicate discriminators");
    }

    this.rawType = requireNonNull(rawType);
    this.discriminator = discriminator;
    this.name = name;
    this.description = description;
    this.version = version;
    this.subcommands = unmodifiableList(subcommands);
  }

  public Class<T> getRawType() {
    return rawType;
  }

  /**
   * @return the discriminator
   */
  public Optional<Discriminator> getDiscriminator() {
    return Optional.ofNullable(discriminator);
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * @return the version
   */
  public String getVersion() {
    return version;
  }

  public List<SubcommandClass<? extends T>> getSubcommands() {
    return subcommands;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ConfigurationClass<?> that)) {
      return false;
    }
    return Objects.equals(getRawType(), that.getRawType()) && Objects.equals(getDiscriminator(),
        that.getDiscriminator()) && Objects.equals(getName(), that.getName()) && Objects.equals(
        getDescription(), that.getDescription()) && Objects.equals(getVersion(), that.getVersion())
        && Objects.equals(getSubcommands(), that.getSubcommands());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getRawType(), getDiscriminator(), getName(), getDescription(), getVersion(),
        getSubcommands());
  }

  @Override
  public String toString() {
    return "ConfigurationClass{" + "rawType=" + rawType + ", discriminator=" + discriminator
        + ", name='" + name + '\'' + ", description='" + description + '\'' + ", version='"
        + version + '\'' + ", subcommands=" + subcommands + '}';
  }

  // SUBCOMMANDS ///////////////////////////////////////////////////////////////////////////////////
  private static <T> List<SubcommandClass<? extends T>> subcommands(Class<T> rawType,
      Configurable configurable) {
    final List<SubcommandClass<? extends T>> result;
    if (configurable.subcommands().length > 0) {
      result = subcommandsFromAnnotations(rawType, configurable);
    } else if (rawType.isSealed()) {
      result = subcommandsFromPermittedSubclasses(rawType, configurable);
    } else {
      result = List.of();
    }
    if (result.isEmpty()) {
      return result;
    }

    return result;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private static <T> List<SubcommandClass<? extends T>> subcommandsFromAnnotations(Class<T> rawType,
      Configurable configurable) {
    if (rawType.isSealed()) {
      throw new SealedSubcommandsConfigurationException(rawType);
    }

    // We have confirmed that each subclass extends the given raw type T, so this is safe.
    return (List) Stream.of(configurable.subcommands()).map(subcommand -> {
      Class<?> rawSubcommandType = subcommand.configurable();
      if (!rawSubcommandType.getSuperclass().equals(rawType)) {
        throw new SubcommandDoesNotExtendRootCommandConfigurationException(rawType,
            subcommand.configurable());
      }

      if (subcommand.discriminator().isEmpty()) {
        throw new NoDiscriminatorConfigurationException(rawSubcommandType);
      }
      Discriminator discriminator;
      try {
        discriminator = Discriminator.fromString(subcommand.discriminator());
      } catch (IllegalArgumentException e) {
        throw new InvalidDiscriminatorConfigurationException(rawType, subcommand.discriminator());
      }

      return (SubcommandClass<? extends T>) new SubcommandClass<>(Optional.of(discriminator),
          rawSubcommandType);
    }).toList();
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private static <T> List<SubcommandClass<? extends T>> subcommandsFromPermittedSubclasses(
      Class<?> rawType, Configurable configurable) {
    if (configurable.subcommands().length > 0) {
      throw new SealedSubcommandsConfigurationException(rawType);
    }
    // We know that each subclass extends the given raw type T, so this is safe.
    return (List) Stream.of(rawType.getPermittedSubclasses())
        .map(permittedSubclass -> new SubcommandClass<>(Optional.empty(), permittedSubclass))
        .toList();
  }

  private static <T> Optional<RuntimeException> validateSubcommands(Class<T> rawType,
      List<SubcommandClass<? extends T>> subcommands) {
    RuntimeException duplicateDiscriminatorException = Streams.duplicates(
            subcommands.stream().map(SubcommandClass::discriminator).flatMap(Optional::stream))
        .findFirst().map(DuplicateDiscriminatorConfigurationException::new).orElse(null);
    if (duplicateDiscriminatorException != null) {
      return Optional.of(duplicateDiscriminatorException);
    }

    return Optional.empty();
  }
}
