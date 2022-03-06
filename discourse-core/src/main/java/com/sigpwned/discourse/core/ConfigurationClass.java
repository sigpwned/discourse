package com.sigpwned.discourse.core;

import static java.lang.String.format;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
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
import com.sigpwned.discourse.core.exception.configuration.NoNameConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.TooManyAnnotationsConfigurationException;
import com.sigpwned.discourse.core.property.EnvironmentConfigurationProperty;
import com.sigpwned.discourse.core.property.FlagConfigurationProperty;
import com.sigpwned.discourse.core.property.OptionConfigurationProperty;
import com.sigpwned.discourse.core.property.PositionalConfigurationProperty;
import com.sigpwned.discourse.core.property.PropertyConfigurationProperty;
import com.sigpwned.espresso.BeanClass;
import com.sigpwned.espresso.BeanInstance;
import com.sigpwned.espresso.BeanProperty;

public class ConfigurationClass {
  public static ConfigurationClass scan(StorageContext storage, Class<?> rawType) {
    BeanClass beanClass = BeanClass.scan(rawType);

    ConfigurationClass result = new ConfigurationClass(beanClass);

    Set<Coordinate> seenCoordinates = new HashSet<>();
    for (BeanProperty beanProperty : beanClass) {
      List<Annotation> annotations = beanProperty.getAnnotations();

      var parameterAnnotations = annotations.stream()
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

      var storer = storage.getStorer(beanProperty);

      var parameterAnnotation = parameterAnnotations.get(0);

      ConfigurationProperty configurationProperty;
      if (parameterAnnotation instanceof EnvironmentParameter) {
        EnvironmentParameter environment = (EnvironmentParameter) parameterAnnotation;

        VariableNameCoordinate variableName;
        try {
          variableName = VariableNameCoordinate.fromString(environment.variableName());
        } catch (IllegalArgumentException e) {
          throw new InvalidVariableNameConfigurationException(environment.variableName());
        }

        configurationProperty = new EnvironmentConfigurationProperty(result, beanProperty, storer,
            environment.description(), variableName, environment.required());
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

        configurationProperty = new FlagConfigurationProperty(result, beanProperty, storer,
            flag.description(), shortName, longName);
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

        configurationProperty = new OptionConfigurationProperty(result, beanProperty, storer,
            option.description(), shortName, longName, option.required());
      } else if (parameterAnnotation instanceof PositionalParameter) {
        PositionalParameter positional = (PositionalParameter) parameterAnnotation;

        PositionCoordinate position;
        try {
          position = PositionCoordinate.of(positional.position());
        } catch (IllegalArgumentException e) {
          throw new InvalidPositionConfigurationException(positional.position());
        }

        configurationProperty = new PositionalConfigurationProperty(result, beanProperty, storer,
            positional.description(), position, positional.required());
      } else if (parameterAnnotation instanceof PropertyParameter) {
        PropertyParameter property = (PropertyParameter) parameterAnnotation;

        PropertyNameCoordinate propertyName;
        try {
          propertyName = PropertyNameCoordinate.fromString(property.propertyName());
        } catch (IllegalArgumentException e) {
          throw new InvalidPropertyNameConfigurationException(property.propertyName());
        }

        configurationProperty = new PropertyConfigurationProperty(result, beanProperty, storer,
            property.description(), propertyName, property.required());
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

      result.addProperty(configurationProperty);
    }

    SortedSet<PositionCoordinate> positions =
        result.getProperties().stream().flatMap(p -> p.getCoordinates().stream())
            .filter(c -> c.getFlavor() == Coordinate.Flavor.POSITION).map(Coordinate::asPosition)
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
        PositionalConfigurationProperty positional = (PositionalConfigurationProperty) result
            .resolve(currentPosition).orElseThrow(() -> new AssertionError(
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

    return result;
  }

  private final BeanClass beanClass;
  private final List<ConfigurationProperty> properties;

  private ConfigurationClass(BeanClass beanClass) {
    this.beanClass = beanClass;
    this.properties = new ArrayList<>();
  }

  /**
   * @return the beanClass
   */
  private BeanClass getBeanClass() {
    return beanClass;
  }

  private void addProperty(ConfigurationProperty property) {
    // If we have a short name, make sure it isn't a duplicate
    Set<Coordinate> coordinates =
        getProperties().stream().flatMap(p -> p.getCoordinates().stream()).collect(toSet());

    for (Coordinate coordinate : property.getCoordinates())
      if (coordinates.contains(coordinate))
        throw new IllegalArgumentException(
            format("Coordinate %s is defined more than once", coordinate));

    properties.add(property);
  }

  public Optional<ConfigurationProperty> resolve(Coordinate coordinate) {
    if (coordinate == null)
      throw new NullPointerException();
    return getProperties().stream().filter(p -> p.getCoordinates().contains(coordinate))
        .findFirst();
  }

  public List<ConfigurationProperty> getProperties() {
    return unmodifiableList(properties);
  }

  public BeanInstance newInstance() throws InvocationTargetException {
    return getBeanClass().newInstance();
  }

  @Override
  public int hashCode() {
    return Objects.hash(beanClass);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ConfigurationClass other = (ConfigurationClass) obj;
    return Objects.equals(beanClass, other.beanClass);
  }
}
