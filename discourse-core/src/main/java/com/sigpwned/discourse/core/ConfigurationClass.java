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

import static java.lang.String.*;
import static java.util.Collections.*;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toMap;

import com.sigpwned.discourse.core.annotation.Configurable;
import com.sigpwned.discourse.core.annotation.EnvironmentParameter;
import com.sigpwned.discourse.core.annotation.FlagParameter;
import com.sigpwned.discourse.core.annotation.OptionParameter;
import com.sigpwned.discourse.core.annotation.PositionalParameter;
import com.sigpwned.discourse.core.annotation.PropertyParameter;
import com.sigpwned.discourse.core.coordinate.Coordinate;
import com.sigpwned.discourse.core.coordinate.LongSwitchNameCoordinate;
import com.sigpwned.discourse.core.coordinate.NameCoordinate;
import com.sigpwned.discourse.core.coordinate.PositionCoordinate;
import com.sigpwned.discourse.core.coordinate.PropertyNameCoordinate;
import com.sigpwned.discourse.core.coordinate.ShortSwitchNameCoordinate;
import com.sigpwned.discourse.core.coordinate.VariableNameCoordinate;
import com.sigpwned.discourse.core.exception.argument.NewInstanceFailureArgumentException;
import com.sigpwned.discourse.core.exception.configuration.DuplicateCoordinateConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.DuplicateDiscriminatorConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.InvalidCollectionParameterPlacementConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.InvalidLongNameConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.InvalidPositionConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.InvalidPropertyNameConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.InvalidRequiredParameterPlacementConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.InvalidShortNameConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.InvalidVariableNameConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.MissingPositionConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.MultipleHelpFlagsConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.MultipleVersionFlagsConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.NoNameConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.NotConfigurableConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.SealedSubcommandsConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.TooManyAnnotationsConfigurationException;
import com.sigpwned.discourse.core.parameter.ConfigurationParameter;
import com.sigpwned.discourse.core.parameter.EnvironmentConfigurationParameter;
import com.sigpwned.discourse.core.parameter.FlagConfigurationParameter;
import com.sigpwned.discourse.core.parameter.OptionConfigurationParameter;
import com.sigpwned.discourse.core.parameter.PositionalConfigurationParameter;
import com.sigpwned.discourse.core.parameter.PropertyConfigurationParameter;
import com.sigpwned.discourse.core.util.ConfigurationParameters;
import com.sigpwned.discourse.core.util.Streams;
import com.sigpwned.espresso.BeanClass;
import com.sigpwned.espresso.BeanInstance;
import com.sigpwned.espresso.BeanProperty;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Stream;

public class ConfigurationClass {

  public static record SubcommandClass(Optional<Discriminator> discriminator, Class<?> rawType) {

    public SubcommandClass {
      discriminator = requireNonNull(discriminator);
      rawType = requireNonNull(rawType);
    }
  }

  public static ConfigurationClass scan(SinkContext storage, SerializationContext serialization,
      Class<?> rawType) {
    final Configurable configurable = rawType.getAnnotation(Configurable.class);
    if (configurable == null) {
      throw new NotConfigurableConfigurationException(rawType);
    }

    final String name = Optional.of(configurable.name()).filter(s -> !s.isEmpty()).orElse(null);

    final String description = Optional.of(configurable.description()).filter(s -> !s.isEmpty())
        .orElse(null);

    final String version = Optional.of(configurable.version()).filter(s -> !s.isEmpty())
        .orElse(null);

    final BeanClass beanClass = BeanClass.scan(rawType);

    final List<ConfigurationParameter> parameters = beanClass.stream()
        .flatMap(p -> parameter(rawType, p, serialization, storage).stream()).toList();

    validateCoordinates(rawType, parameters.stream()
        .flatMap(p -> p.getCoordinates().stream().map(c -> Map.entry(c, p)))).ifPresent(e -> {
      throw e;
    });

    final Discriminator discriminator = Optional.of(configurable.discriminator())
        .filter(s -> !s.isEmpty()).map(Discriminator::fromString).orElse(null);

    final List<SubcommandClass> subcommands = subcommands(rawType, configurable);

    validateSubcommands(rawType, subcommands).ifPresent(e -> {
      throw e;
    });

    return new ConfigurationClass(beanClass, discriminator, name, description, version, parameters,
        subcommands);
  }

  private final BeanClass beanClass;
  private final Discriminator discriminator;
  private final String name;
  private final String description;
  private final String version;
  private final List<ConfigurationParameter> parameters;
  private final List<SubcommandClass> subcommands;

  private ConfigurationClass(BeanClass beanClass, Discriminator discriminator, String name,
      String description, String version, List<ConfigurationParameter> parameters,
      List<SubcommandClass> subcommands) {
    Streams.duplicates(parameters.stream()
            .map(p -> p.getCoordinates().stream().map(c -> Map.entry(c, p.getName())))).findFirst()
        .ifPresent(c -> {
          throw new IllegalArgumentException(format("coordinates defined more than once: %s", c));
        });

    if (parameters.stream().mapMulti(ConfigurationParameters.mapMultiFlag())
        .filter(FlagConfigurationParameter::isHelp).count() > 1L) {
      throw new IllegalArgumentException("multiple help flags");
    }

    if (parameters.stream().mapMulti(ConfigurationParameters.mapMultiFlag())
        .filter(FlagConfigurationParameter::isVersion).count() > 1L) {
      throw new IllegalArgumentException("multiple version flags");
    }

    if (Streams.duplicates(
            subcommands.stream().map(SubcommandClass::discriminator).flatMap(Optional::stream))
        .findFirst().isPresent()) {
      throw new IllegalArgumentException("duplicate discriminators");
    }

    this.beanClass = requireNonNull(beanClass);
    this.discriminator = discriminator;
    this.name = name;
    this.description = description;
    this.version = version;
    this.parameters = unmodifiableList(parameters);
    this.subcommands = unmodifiableList(subcommands);
  }

  public Class<?> getRawType() {
    return getBeanClass().getRawType();
  }

  /**
   * @return the beanClass
   */
  private BeanClass getBeanClass() {
    return beanClass;
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

  public Optional<ConfigurationParameter> resolve(Coordinate coordinate) {
    if (coordinate == null) {
      throw new NullPointerException();
    }
    return getParameters().stream().filter(p -> p.getCoordinates().contains(coordinate))
        .findFirst();
  }

  public List<ConfigurationParameter> getParameters() {
    return parameters;
  }

  public List<SubcommandClass> getSubcommands() {
    return subcommands;
  }

  public BeanInstance newInstance() {
    try {
      return getBeanClass().newInstance();
    } catch (InvocationTargetException e) {
      throw new NewInstanceFailureArgumentException(e);
    }
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ConfigurationClass that)) {
      return false;
    }
    return Objects.equals(getBeanClass(), that.getBeanClass()) && Objects.equals(getDiscriminator(),
        that.getDiscriminator()) && Objects.equals(getName(), that.getName()) && Objects.equals(
        getDescription(), that.getDescription()) && Objects.equals(getVersion(), that.getVersion())
        && Objects.equals(getParameters(), that.getParameters());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getBeanClass(), getDiscriminator(), getName(), getDescription(),
        getVersion(), getParameters());
  }

  @Override
  public String toString() {
    return "ConfigurationClass{" + "beanClass=" + beanClass + ", discriminator=" + discriminator
        + ", name='" + name + '\'' + ", description='" + description + '\'' + ", version='"
        + version + '\'' + ", parameters=" + parameters + '}';
  }

  // SUBCOMMANDS ///////////////////////////////////////////////////////////////////////////////////
  private static List<SubcommandClass> subcommands(Class<?> rawType, Configurable configurable) {
    final List<SubcommandClass> result;
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

  private static List<SubcommandClass> subcommandsFromAnnotations(Class<?> rawType,
      Configurable configurable) {
    if (rawType.isSealed()) {
      throw new SealedSubcommandsConfigurationException(rawType);
    }
    return Stream.of(configurable.subcommands()).map(subcommand -> new SubcommandClass(
        Optional.of(Discriminator.fromString(subcommand.discriminator())), rawType)).toList();
  }

  private static List<SubcommandClass> subcommandsFromPermittedSubclasses(Class<?> rawType,
      Configurable configurable) {
    if (configurable.subcommands().length > 0) {
      throw new SealedSubcommandsConfigurationException(rawType);
    }
    return Stream.of(rawType.getPermittedSubclasses())
        .map(permittedSubclass -> new SubcommandClass(Optional.empty(), permittedSubclass))
        .toList();
  }

  private static Optional<RuntimeException> validateSubcommands(Class<?> rawType,
      List<SubcommandClass> subcommands) {
    RuntimeException duplicateDiscriminatorException = Streams.duplicates(
            subcommands.stream().map(SubcommandClass::discriminator).flatMap(Optional::stream))
        .findFirst().map(DuplicateDiscriminatorConfigurationException::new).orElse(null);
    if (duplicateDiscriminatorException != null) {
      return Optional.of(duplicateDiscriminatorException);
    }

    return Optional.empty();
  }

  // COORDINATES ///////////////////////////////////////////////////////////////////////////////////

  @SuppressWarnings({"rawtypes", "unchecked"})
  private static Optional<RuntimeException> validateCoordinates(Class<?> rawType,
      Stream<Map.Entry<Coordinate, ConfigurationParameter>> stream) {
    List<Map.Entry<Coordinate, ConfigurationParameter>> entries = stream.toList();

    RuntimeException nameProblem = validateNameCoordinates(rawType,
        entries.stream().mapMulti((e, downstream) -> {
          if (e.getKey() instanceof NameCoordinate name) {
            downstream.accept((Map.Entry) e);
          }
        })).orElse(null);
    if (nameProblem != null) {
      return Optional.of(nameProblem);
    }

    RuntimeException positionProblem = validatePositionCoordinates(rawType,
        entries.stream().mapMulti((e, downstream) -> {
          if (e.getKey() instanceof PositionCoordinate position) {
            downstream.accept((Map.Entry) e);
          }
        })).orElse(null);
    if (positionProblem != null) {
      return Optional.of(positionProblem);
    }

    return Optional.empty();
  }

  private static Optional<RuntimeException> validateNameCoordinates(Class<?> rawType,
      Stream<Map.Entry<NameCoordinate, ConfigurationParameter>> stream) {
    List<Map.Entry<NameCoordinate, ConfigurationParameter>> parameters = stream.toList();

    // Is there more than 1 help flag?
    if (parameters.stream().map(Map.Entry::getValue)
        .mapMulti(ConfigurationParameters.mapMultiFlag()).filter(FlagConfigurationParameter::isHelp)
        .count() > 1L) {
      return Optional.of(new MultipleHelpFlagsConfigurationException(rawType));
    }

    // Is there more than 1 version flag?
    if (parameters.stream().map(Map.Entry::getValue)
        .mapMulti(ConfigurationParameters.mapMultiFlag())
        .filter(FlagConfigurationParameter::isVersion).count() > 1L) {
      return Optional.of(new MultipleVersionFlagsConfigurationException(rawType));
    }

    // Are there any duplicate names?
    RuntimeException duplicateCoordinateException = Streams.duplicates(
            parameters.stream().map(Map.Entry::getKey)).findFirst()
        .map(DuplicateCoordinateConfigurationException::new).orElse(null);
    if (duplicateCoordinateException != null) {
      return Optional.of(duplicateCoordinateException);
    }

    return Optional.empty();
  }

  private static Optional<RuntimeException> validatePositionCoordinates(Class<?> rawType,
      Stream<Map.Entry<PositionCoordinate, ConfigurationParameter>> stream) {
    // Are the positional coordinates well-formed?
    List<Map.Entry<PositionCoordinate, ConfigurationParameter>> ps = stream.toList();
    if (ps.isEmpty()) {
      return Optional.empty();
    }

    RuntimeException duplicateCoordinateException = Streams.duplicates(
            ps.stream().map(Map.Entry::getKey)).findFirst()
        .map(DuplicateCoordinateConfigurationException::new).orElse(null);
    if (duplicateCoordinateException != null) {
      return Optional.of(duplicateCoordinateException);
    }

    SortedMap<PositionCoordinate, ConfigurationParameter> parameters = ps.stream()
        .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> {
          throw new AssertionError("duplicate key");
        }, TreeMap::new));

    // Is the first positional coordinate the "zero" position?
    if (!parameters.firstKey().equals(PositionCoordinate.ZERO)) {
      return Optional.of(new MissingPositionConfigurationException(0));
    }

    // Are there any missing positions?
    RuntimeException missingPositionException = parameters.headMap(parameters.lastKey()).keySet()
        .stream().filter(p -> !parameters.containsKey(p.next())).findFirst()
        .map(p -> new MissingPositionConfigurationException(p.next().getIndex())).orElse(null);
    if (missingPositionException != null) {
      return Optional.of(missingPositionException);
    }

    // Are there any required parameters after the first optional parameter?
    RuntimeException invalidRequiredParameterPlacementException = parameters.entrySet().stream()
        .dropWhile(e -> e.getValue().isRequired()).dropWhile(e -> !e.getValue().isRequired())
        .findFirst().map(
            e -> new InvalidRequiredParameterPlacementConfigurationException(e.getKey().getIndex()))
        .orElse(null);
    if (invalidRequiredParameterPlacementException != null) {
      return Optional.of(invalidRequiredParameterPlacementException);
    }

    // Are there any collections before the last parameter?
    RuntimeException invalidCollectionParameterPlacementException = parameters.headMap(
            parameters.lastKey()).entrySet().stream().filter(e -> e.getValue().isCollection())
        .findFirst().map(e -> new InvalidCollectionParameterPlacementConfigurationException(
            e.getKey().getIndex())).orElse(null);
    if (invalidCollectionParameterPlacementException != null) {
      return Optional.of(invalidCollectionParameterPlacementException);
    }

    return Optional.empty();
  }

  // PARAMETERS ////////////////////////////////////////////////////////////////////////////////////

  private static Optional<ConfigurationParameter> parameter(Class<?> rawType,
      BeanProperty beanProperty, SerializationContext serialization, SinkContext storage) {
    ValueSink sink = storage.getSink(beanProperty);

    ValueDeserializer<?> deserializer = serialization.getDeserializer(sink.getGenericType(),
        beanProperty.getAnnotations()).orElseThrow(
        () -> new RuntimeException("No deserializer for property " + beanProperty.getName()));

    ConfigurationParameter configurationProperty = null;
    for (Annotation annotation : beanProperty.getAnnotations()) {
      ConfigurationParameter p = null;
      if (annotation instanceof FlagParameter flag) {
        p = flagParameter(beanProperty, flag, deserializer, sink);
      } else if (annotation instanceof EnvironmentParameter environment) {
        p = environmentParameter(beanProperty, environment, deserializer, sink);
      } else if (annotation instanceof OptionParameter option) {
        p = optionParameter(beanProperty, option, deserializer, sink);
      } else if (annotation instanceof PositionalParameter positional) {
        p = positionalParameter(beanProperty, positional, deserializer, sink);
      } else if (annotation instanceof PropertyParameter property) {
        p = propertyParameter(beanProperty, property, deserializer, sink);
      }

      if (p != null) {
        if (configurationProperty != null) {
          throw new TooManyAnnotationsConfigurationException(beanProperty.getName());
        }
        configurationProperty = p;
      }
    }

    return Optional.ofNullable(configurationProperty);
  }

  private static ConfigurationParameter propertyParameter(BeanProperty property,
      PropertyParameter parameter, ValueDeserializer<?> deserializer, ValueSink sink) {
    PropertyNameCoordinate propertyName;
    try {
      propertyName = PropertyNameCoordinate.fromString(parameter.propertyName());
    } catch (IllegalArgumentException e) {
      throw new InvalidPropertyNameConfigurationException(parameter.propertyName());
    }

    return new PropertyConfigurationParameter(property.getName(), parameter.description(),
        parameter.required(), deserializer, sink, propertyName);
  }

  private static ConfigurationParameter positionalParameter(BeanProperty property,
      PositionalParameter parameter, ValueDeserializer<?> deserializer, ValueSink sink) {
    PositionCoordinate position;
    try {
      position = PositionCoordinate.of(parameter.position());
    } catch (IllegalArgumentException e) {
      throw new InvalidPositionConfigurationException(parameter.position());
    }

    return new PositionalConfigurationParameter(property.getName(), parameter.description(),
        parameter.required(), deserializer, sink, position);
  }

  private static ConfigurationParameter optionParameter(BeanProperty property,
      OptionParameter parameter, ValueDeserializer<?> deserializer, ValueSink sink) {
    ShortSwitchNameCoordinate shortName;
    if (parameter.shortName().isEmpty()) {
      shortName = null;
    } else {
      try {
        shortName = ShortSwitchNameCoordinate.fromString(parameter.shortName());
      } catch (IllegalArgumentException e) {
        throw new InvalidShortNameConfigurationException(parameter.shortName());
      }
    }

    LongSwitchNameCoordinate longName;
    if (parameter.longName().isEmpty()) {
      longName = null;
    } else {
      try {
        longName = LongSwitchNameCoordinate.fromString(parameter.longName());
      } catch (IllegalArgumentException e) {
        throw new InvalidLongNameConfigurationException(parameter.longName());
      }
    }

    if (shortName == null && longName == null) {
      throw new NoNameConfigurationException(property.getName());
    }

    return new OptionConfigurationParameter(property.getName(), parameter.description(),
        parameter.required(), deserializer, sink, shortName, longName);
  }

  private static ConfigurationParameter flagParameter(BeanProperty property,
      FlagParameter parameter, ValueDeserializer<?> deserializer, ValueSink sink) {
    ShortSwitchNameCoordinate shortName;
    if (parameter.shortName().isEmpty()) {
      shortName = null;
    } else {
      try {
        shortName = ShortSwitchNameCoordinate.fromString(parameter.shortName());
      } catch (IllegalArgumentException e) {
        throw new InvalidShortNameConfigurationException(parameter.shortName());
      }
    }

    LongSwitchNameCoordinate longName;
    if (parameter.longName().isEmpty()) {
      longName = null;
    } else {
      try {
        longName = LongSwitchNameCoordinate.fromString(parameter.longName());
      } catch (IllegalArgumentException e) {
        throw new InvalidLongNameConfigurationException(parameter.longName());
      }
    }

    if (shortName == null && longName == null) {
      throw new NoNameConfigurationException(property.getName());
    }

    return new FlagConfigurationParameter(property.getName(), parameter.description(), deserializer,
        sink, shortName, longName, parameter.help(), parameter.version());
  }

  private static ConfigurationParameter environmentParameter(BeanProperty property,
      EnvironmentParameter parameter, ValueDeserializer<?> deserializer, ValueSink sink) {
    VariableNameCoordinate variableName;
    try {
      variableName = VariableNameCoordinate.fromString(parameter.variableName());
    } catch (IllegalArgumentException e) {
      throw new InvalidVariableNameConfigurationException(parameter.variableName());
    }

    return new EnvironmentConfigurationParameter(property.getName(), parameter.description(),
        parameter.required(), deserializer, sink, variableName);
  }
}
