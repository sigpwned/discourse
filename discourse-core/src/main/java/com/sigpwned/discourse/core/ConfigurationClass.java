package com.sigpwned.discourse.core;

import static java.lang.String.format;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import com.sigpwned.discourse.core.annotation.Configurable;
import com.sigpwned.discourse.core.annotation.EnvironmentParameter;
import com.sigpwned.discourse.core.annotation.FlagParameter;
import com.sigpwned.discourse.core.annotation.OptionParameter;
import com.sigpwned.discourse.core.annotation.PositionalParameter;
import com.sigpwned.discourse.core.annotation.PropertyParameter;
import com.sigpwned.discourse.core.coordinate.PositionCoordinate;
import com.sigpwned.discourse.core.coordinate.name.PropertyNameCoordinate;
import com.sigpwned.discourse.core.coordinate.name.VariableNameCoordinate;
import com.sigpwned.discourse.core.coordinate.name.switches.LongSwitchNameCoordinate;
import com.sigpwned.discourse.core.coordinate.name.switches.ShortSwitchNameCoordinate;
import com.sigpwned.discourse.core.exception.configuration.DuplicateCoordinateConfigurationException;
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
import com.sigpwned.discourse.core.exception.configuration.TooManyAnnotationsConfigurationException;
import com.sigpwned.discourse.core.parameter.EnvironmentConfigurationParameter;
import com.sigpwned.discourse.core.parameter.FlagConfigurationParameter;
import com.sigpwned.discourse.core.parameter.OptionConfigurationParameter;
import com.sigpwned.discourse.core.parameter.PositionalConfigurationParameter;
import com.sigpwned.discourse.core.parameter.PropertyConfigurationParameter;
import com.sigpwned.discourse.core.util.Generated;
import com.sigpwned.espresso.BeanClass;
import com.sigpwned.espresso.BeanInstance;
import com.sigpwned.espresso.BeanProperty;

public class ConfigurationClass {
  public static ConfigurationClass scan(SinkContext storage, SerializationContext serialization,
      Class<?> rawType) {
    Configurable configurable = rawType.getAnnotation(Configurable.class);
    if (configurable == null)
      throw new NotConfigurableConfigurationException(rawType);

    // TODO throw multi configuration exceptions here

    final String name = configurable.name().isEmpty() ? null : configurable.name();

    final String description =
        configurable.description().isEmpty() ? null : configurable.description();

    final String version = configurable.version().isEmpty() ? null : configurable.version();

    BeanClass beanClass = BeanClass.scan(rawType);

    boolean seenHelp = false, seenVersion = false;
    List<ConfigurationParameter> parameters = new ArrayList<>();
    Set<Coordinate> seenCoordinates = new HashSet<>();
    for (BeanProperty beanProperty : beanClass) {
      List<Annotation> annotations = beanProperty.getAnnotations();

      List<Annotation> parameterAnnotations = annotations.stream()
          .filter(a -> a instanceof EnvironmentParameter || a instanceof FlagParameter
              || a instanceof OptionParameter || a instanceof PositionalParameter
              || a instanceof PropertyParameter)
          .collect(toList());

      if (parameterAnnotations.isEmpty()) {
        // This is fine. This property just isn't configurable. No problem.
        continue;
      }

      if (parameterAnnotations.size() > 1) {
        // We have multiple directives for how to parse. This isn't OK.
        throw new TooManyAnnotationsConfigurationException(beanProperty.getName());
      }

      String parameterName = beanProperty.getName();

      ValueSink sink = storage.getSink(beanProperty);

      ValueDeserializer<?> deserializer = serialization
          .getDeserializer(sink.getGenericType(), beanProperty.getAnnotations())
          .orElseThrow(() -> new RuntimeException("No deserializer for property " + parameterName));

      Annotation parameterAnnotation = parameterAnnotations.get(0);

      ConfigurationParameter configurationProperty;
      if (parameterAnnotation instanceof EnvironmentParameter) {
        EnvironmentParameter environment = (EnvironmentParameter) parameterAnnotation;

        VariableNameCoordinate variableName;
        try {
          variableName = VariableNameCoordinate.fromString(environment.variableName());
        } catch (IllegalArgumentException e) {
          throw new InvalidVariableNameConfigurationException(environment.variableName());
        }

        configurationProperty = new EnvironmentConfigurationParameter(parameterName,
            environment.description(), environment.required(), deserializer, sink, variableName);
      } else if (parameterAnnotation instanceof FlagParameter) {
        FlagParameter flag = (FlagParameter) parameterAnnotation;

        ShortSwitchNameCoordinate shortName;
        if (flag.shortName().isEmpty()) {
          shortName = null;
        } else {
          try {
            shortName = ShortSwitchNameCoordinate.fromString(flag.shortName());
          } catch (IllegalArgumentException e) {
            throw new InvalidShortNameConfigurationException(flag.shortName());
          }
        }

        LongSwitchNameCoordinate longName;
        if (flag.longName().isEmpty()) {
          longName = null;
        } else {
          try {
            longName = LongSwitchNameCoordinate.fromString(flag.longName());
          } catch (IllegalArgumentException e) {
            throw new InvalidLongNameConfigurationException(flag.longName());
          }
        }

        if (shortName == null && longName == null)
          throw new NoNameConfigurationException(beanProperty.getName());

        if (seenHelp && flag.help())
          throw new MultipleHelpFlagsConfigurationException(rawType);

        seenHelp = seenHelp || flag.help();

        if (seenVersion && flag.version())
          throw new MultipleVersionFlagsConfigurationException(rawType);

        seenVersion = seenVersion || flag.version();

        configurationProperty = new FlagConfigurationParameter(parameterName, flag.description(),
            deserializer, sink, shortName, longName, flag.help(), flag.version());
      } else if (parameterAnnotation instanceof OptionParameter) {
        OptionParameter option = (OptionParameter) parameterAnnotation;

        ShortSwitchNameCoordinate shortName;
        if (option.shortName().isEmpty()) {
          shortName = null;
        } else {
          try {
            shortName = ShortSwitchNameCoordinate.fromString(option.shortName());
          } catch (IllegalArgumentException e) {
            throw new InvalidShortNameConfigurationException(option.shortName());
          }
        }

        LongSwitchNameCoordinate longName;
        if (option.longName().isEmpty()) {
          longName = null;
        } else {
          try {
            longName = LongSwitchNameCoordinate.fromString(option.longName());
          } catch (IllegalArgumentException e) {
            throw new InvalidLongNameConfigurationException(option.longName());
          }
        }

        if (shortName == null && longName == null)
          throw new NoNameConfigurationException(beanProperty.getName());

        configurationProperty = new OptionConfigurationParameter(parameterName,
            option.description(), option.required(), deserializer, sink, shortName, longName);
      } else if (parameterAnnotation instanceof PositionalParameter) {
        PositionalParameter positional = (PositionalParameter) parameterAnnotation;

        PositionCoordinate position;
        try {
          position = PositionCoordinate.of(positional.position());
        } catch (IllegalArgumentException e) {
          throw new InvalidPositionConfigurationException(positional.position());
        }

        configurationProperty = new PositionalConfigurationParameter(parameterName,
            positional.description(), positional.required(), deserializer, sink, position);
      } else if (parameterAnnotation instanceof PropertyParameter) {
        PropertyParameter property = (PropertyParameter) parameterAnnotation;

        PropertyNameCoordinate propertyName;
        try {
          propertyName = PropertyNameCoordinate.fromString(property.propertyName());
        } catch (IllegalArgumentException e) {
          throw new InvalidPropertyNameConfigurationException(property.propertyName());
        }

        configurationProperty = new PropertyConfigurationParameter(parameterName,
            property.description(), property.required(), deserializer, sink, propertyName);
      } else {
        throw new AssertionError(
            format("Failed to recognize Configuration class %s property %s parameter type",
                rawType.getName(), beanProperty.getName()));
      }

      for (Coordinate coordinate : configurationProperty.getCoordinates()) {
        if (seenCoordinates.contains(coordinate))
          throw new DuplicateCoordinateConfigurationException(coordinate);
        seenCoordinates.add(coordinate);
      }

      parameters.add(configurationProperty);
    }

    SortedSet<PositionCoordinate> positions =
        parameters.stream().flatMap(p -> p.getCoordinates().stream())
            .filter(c -> c.getFamily() == Coordinate.Family.POSITION).map(Coordinate::asPosition)
            .collect(toCollection(TreeSet::new));
    if (positions.isEmpty()) {
      // No positional arguments. That's a-OK.
    } else {
      PositionCoordinate previousPosition = null;
      boolean seenOptionalParameter = false;
      for (PositionCoordinate currentPosition : positions) {
        boolean firstPosition = currentPosition.equals(positions.first());
        boolean lastPosition = currentPosition.equals(positions.last());

        if (!firstPosition && !currentPosition.equals(previousPosition.next()))
          throw new MissingPositionConfigurationException(previousPosition.next().getIndex());

        if (firstPosition && !currentPosition.equals(PositionCoordinate.ZERO))
          throw new MissingPositionConfigurationException(0);

        final int index = currentPosition.getIndex();
        PositionalConfigurationParameter positional =
            parameters.stream().filter(p -> p.getCoordinates().contains(currentPosition))
                .map(ConfigurationParameter::asPositional).findFirst()
                .orElseThrow(() -> new AssertionError(
                    format("Failed to retrieve parameter for position %d", index)));

        if (positional.isRequired() && seenOptionalParameter)
          throw new InvalidRequiredParameterPlacementConfigurationException(
              currentPosition.getIndex());

        if (positional.isCollection() && !lastPosition)
          throw new InvalidCollectionParameterPlacementConfigurationException(
              currentPosition.getIndex());

        seenOptionalParameter = seenOptionalParameter || !positional.isRequired();

        previousPosition = currentPosition;
      }
    }

    return new ConfigurationClass(beanClass, name, description, version, parameters);
  }

  private final BeanClass beanClass;
  private final String name;
  private final String description;
  private final String version;
  private final List<ConfigurationParameter> parameters;

  private ConfigurationClass(BeanClass beanClass, String name, String description, String version,
      List<ConfigurationParameter> parameters) {
    Map<Coordinate, List<String>> coordinates = parameters.stream()
        .flatMap(p -> p.getCoordinates().stream()
            .map(c -> new SimpleImmutableEntry<Coordinate, String>(c, p.getName())))
        .collect(groupingBy(e -> e.getKey(), mapping(e -> e.getValue(), toList())));
    Set<Coordinate> duplicateCoordinates = coordinates.entrySet().stream()
        .filter(e -> e.getValue().size() > 1).map(e -> e.getKey()).collect(toSet());
    if (!duplicateCoordinates.isEmpty())
      throw new IllegalArgumentException(
          format("following coordinates defined more than once", duplicateCoordinates));

    if (parameters.stream().filter(p -> p.getType() == ConfigurationParameter.Type.FLAG)
        .map(ConfigurationParameter::asFlag).filter(FlagConfigurationParameter::isHelp)
        .count() > 1L) {
      throw new IllegalArgumentException("multiple help flags");
    }

    if (parameters.stream().filter(p -> p.getType() == ConfigurationParameter.Type.FLAG)
        .map(ConfigurationParameter::asFlag).filter(FlagConfigurationParameter::isVersion)
        .count() > 1L) {
      throw new IllegalArgumentException("multiple version flags");
    }

    this.beanClass = beanClass;
    this.name = name;
    this.description = description;
    this.version = version;
    this.parameters = unmodifiableList(parameters);
  }

  /**
   * @return the beanClass
   */
  private BeanClass getBeanClass() {
    return beanClass;
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
    if (coordinate == null)
      throw new NullPointerException();
    return getParameters().stream().filter(p -> p.getCoordinates().contains(coordinate))
        .findFirst();
  }

  public List<ConfigurationParameter> getParameters() {
    return parameters;
  }

  public BeanInstance newInstance() throws InvocationTargetException {
    return getBeanClass().newInstance();
  }

  @Override
  @Generated
  public int hashCode() {
    return Objects.hash(beanClass, description, name, parameters, version);
  }

  @Override
  @Generated
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ConfigurationClass other = (ConfigurationClass) obj;
    return Objects.equals(beanClass, other.beanClass)
        && Objects.equals(description, other.description) && Objects.equals(name, other.name)
        && Objects.equals(parameters, other.parameters) && Objects.equals(version, other.version);
  }

  @Override
  public String toString() {
    return "ConfigurationClass [beanClass=" + beanClass + ", name=" + name + ", description="
        + description + ", version=" + version + ", parameters=" + parameters + "]";
  }
}
