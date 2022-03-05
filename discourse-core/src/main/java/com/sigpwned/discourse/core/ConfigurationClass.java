package com.sigpwned.discourse.core;

import static java.lang.String.format;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;
import com.sigpwned.discourse.core.annotation.EnvironmentParameter;
import com.sigpwned.discourse.core.annotation.FlagParameter;
import com.sigpwned.discourse.core.annotation.OptionParameter;
import com.sigpwned.discourse.core.annotation.PositionalParameter;
import com.sigpwned.discourse.core.annotation.PropertyParameter;
import com.sigpwned.discourse.core.exception.configuration.InvalidCollectionParameterPlacementConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.InvalidRequiredParameterPlacementConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.MissingPositionConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.TooManyAnnotationsConfigurationException;
import com.sigpwned.discourse.core.property.EnvironmentConfigurationProperty;
import com.sigpwned.discourse.core.property.FlagConfigurationProperty;
import com.sigpwned.discourse.core.property.OptionConfigurationProperty;
import com.sigpwned.discourse.core.property.PositionalConfigurationProperty;
import com.sigpwned.discourse.core.property.PropertyConfigurationProperty;
import com.sigpwned.discourse.core.util.Parameters;
import com.sigpwned.espresso.BeanClass;
import com.sigpwned.espresso.BeanInstance;
import com.sigpwned.espresso.BeanProperty;

public class ConfigurationClass {
  public static ConfigurationClass scan(StorageContext storage, Class<?> rawType) {
    BeanClass beanClass = BeanClass.scan(rawType);

    ConfigurationClass result = new ConfigurationClass(beanClass);

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
        configurationProperty = new EnvironmentConfigurationProperty(result, beanProperty, storer,
            environment.description(), environment.variableName(), environment.required());
      } else if (parameterAnnotation instanceof FlagParameter) {
        FlagParameter flag = (FlagParameter) parameterAnnotation;
        configurationProperty = new FlagConfigurationProperty(result, beanProperty, storer,
            flag.description(), flag.shortName().isEmpty() ? null : flag.shortName(),
            flag.longName().isEmpty() ? null : flag.longName());
      } else if (parameterAnnotation instanceof OptionParameter) {
        OptionParameter option = (OptionParameter) parameterAnnotation;
        configurationProperty = new OptionConfigurationProperty(result, beanProperty, storer,
            option.description(), option.shortName().isEmpty() ? null : option.shortName(),
            option.longName().isEmpty() ? null : option.longName(), option.required());
      } else if (parameterAnnotation instanceof PositionalParameter) {
        PositionalParameter positional = (PositionalParameter) parameterAnnotation;
        configurationProperty = new PositionalConfigurationProperty(result, beanProperty, storer,
            positional.description(), positional.position(), positional.required());
      } else if (parameterAnnotation instanceof PropertyParameter) {
        PropertyParameter property = (PropertyParameter) parameterAnnotation;
        configurationProperty = new PropertyConfigurationProperty(result, beanProperty, storer,
            property.description(), property.propertyName(), property.required());
      } else {
        throw new AssertionError(
            format("Failed to recognize Configuration class %s property %s parameter type",
                rawType.getName(), beanProperty.getName()));
      }

      result.addProperty(configurationProperty);
    }

    SortedSet<Integer> positions = result.getProperties().stream()
        .flatMap(p -> Parameters.position(p).stream().boxed()).collect(toCollection(TreeSet::new));
    if (positions.isEmpty()) {
      // No positional arguments. That's a-OK.
    } else {
      int previousPosition = -1;
      boolean seenOptionalParameter = false;
      for (int currentPosition : positions) {
        boolean lastPosition = currentPosition == positions.size() - 1;

        if (previousPosition != -1 && currentPosition != previousPosition + 1)
          throw new MissingPositionConfigurationException(previousPosition + 1);
        
        if (previousPosition == -1 && currentPosition != 0)
          throw new MissingPositionConfigurationException(0);

        final int theposition = currentPosition;
        PositionalConfigurationProperty positional = (PositionalConfigurationProperty) result
            .getPropertyByPosition(currentPosition).orElseThrow(() -> new AssertionError(
                format("Failed to retrieve parameter for position %d", theposition)));

        if (positional.isRequired() && seenOptionalParameter)
          throw new InvalidRequiredParameterPlacementConfigurationException(currentPosition);

        if (positional.isCollection() && !lastPosition)
          throw new InvalidCollectionParameterPlacementConfigurationException(currentPosition);

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
    Optional<String> maybeShortName = Parameters.shortName(property);
    if (maybeShortName.isPresent()) {
      if (getProperties().stream().flatMap(p -> Parameters.shortName(p).stream())
          .anyMatch(n -> n.equals(maybeShortName.get()))) {
        throw new IllegalArgumentException(
            format("Short name %s is defined more than once", maybeShortName.get()));
      }
    }

    // If we have a long name, make sure it isn't a duplicate
    Optional<String> maybeLongName = Parameters.longName(property);
    if (maybeLongName.isPresent()) {
      if (getProperties().stream().flatMap(p -> Parameters.longName(p).stream())
          .anyMatch(n -> n.equals(maybeLongName.get()))) {
        throw new IllegalArgumentException(
            format("Long name %s is defined more than once", maybeLongName.get()));
      }
    }

    // If we have a variable name, make sure it isn't a duplicate
    Optional<String> maybeVariableName = Parameters.variableName(property);
    if (maybeVariableName.isPresent()) {
      if (getProperties().stream().flatMap(p -> Parameters.variableName(p).stream())
          .anyMatch(n -> n.equals(maybeVariableName.get()))) {
        throw new IllegalArgumentException(
            format("Environment variable %s is defined more than once", maybeVariableName.get()));
      }
    }

    // If we have a property name, make sure it isn't a duplicate
    Optional<String> maybePropertyName = Parameters.propertyName(property);
    if (maybePropertyName.isPresent()) {
      if (getProperties().stream().flatMap(p -> Parameters.propertyName(p).stream())
          .anyMatch(n -> n.equals(maybePropertyName.get()))) {
        throw new IllegalArgumentException(
            format("System property %s is defined more than once", maybePropertyName.get()));
      }
    }

    properties.add(property);
  }

  public Optional<ConfigurationProperty> getPropertyByShortName(String shortName) {
    if (shortName == null)
      throw new NullPointerException();
    return getProperties().stream()
        .filter(p -> Parameters.shortName(p).filter(n -> n.equals(shortName)).isPresent())
        .findFirst();
  }

  public Optional<ConfigurationProperty> getPropertyByLongName(String longName) {
    if (longName == null)
      throw new NullPointerException();
    return getProperties().stream()
        .filter(p -> Parameters.longName(p).filter(n -> n.equals(longName)).isPresent())
        .findFirst();
  }

  public Optional<ConfigurationProperty> getPropertyByVariableName(String variableName) {
    if (variableName == null)
      throw new NullPointerException();
    return getProperties().stream()
        .filter(p -> Parameters.variableName(p).filter(n -> n.equals(variableName)).isPresent())
        .findFirst();
  }

  public Optional<ConfigurationProperty> getPropertyByPropertyName(String propertyName) {
    if (propertyName == null)
      throw new NullPointerException();
    return getProperties().stream()
        .filter(p -> Parameters.propertyName(p).filter(n -> n.equals(propertyName)).isPresent())
        .findFirst();
  }

  public Optional<ConfigurationProperty> getPropertyByPosition(int position) {
    if (position < 0)
      throw new IllegalArgumentException("position must not be negative");
    return getProperties().stream().filter(p -> Parameters.position(p).orElse(-1) == position)
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
