package com.sigpwned.discourse.core.util;

import static java.util.function.Predicate.not;

import com.sigpwned.discourse.core.value.deserializer.ValueDeserializer;
import com.sigpwned.discourse.core.value.sink.ValueSink;
import com.sigpwned.discourse.core.annotation.EnvironmentParameter;
import com.sigpwned.discourse.core.annotation.FlagParameter;
import com.sigpwned.discourse.core.annotation.OptionParameter;
import com.sigpwned.discourse.core.annotation.PositionalParameter;
import com.sigpwned.discourse.core.annotation.PropertyParameter;
import com.sigpwned.discourse.core.coordinate.LongSwitchNameCoordinate;
import com.sigpwned.discourse.core.coordinate.PositionCoordinate;
import com.sigpwned.discourse.core.coordinate.PropertyNameCoordinate;
import com.sigpwned.discourse.core.coordinate.ShortSwitchNameCoordinate;
import com.sigpwned.discourse.core.coordinate.VariableNameCoordinate;
import com.sigpwned.discourse.core.exception.configuration.InvalidPositionConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.NoNameConfigurationException;
import com.sigpwned.discourse.core.parameter.ConfigurationParameter;
import com.sigpwned.discourse.core.parameter.EnvironmentConfigurationParameter;
import com.sigpwned.discourse.core.parameter.FlagConfigurationParameter;
import com.sigpwned.discourse.core.parameter.OptionConfigurationParameter;
import com.sigpwned.discourse.core.parameter.PositionalConfigurationParameter;
import com.sigpwned.discourse.core.parameter.PropertyConfigurationParameter;
import java.lang.annotation.Annotation;
import java.util.Optional;

public final class ConfigurationParameters {

  private ConfigurationParameters() {
  }

  public static ConfigurationParameter createConfigurationParameter(Annotation parameterAnnotation,
      String name, ValueDeserializer<?> deserializer, ValueSink sink) {
    if (parameterAnnotation instanceof FlagParameter flag) {
      ShortSwitchNameCoordinate shortSwitch = Optional.of(flag.shortName())
          .filter(not(String::isEmpty)).map(ShortSwitchNameCoordinate::fromString).orElse(null);
      LongSwitchNameCoordinate longSwitch = Optional.of(flag.longName())
          .filter(not(String::isEmpty)).map(LongSwitchNameCoordinate::fromString).orElse(null);
      if (shortSwitch == null && longSwitch == null) {
        throw new NoNameConfigurationException(name);
      }
      return new FlagConfigurationParameter(name, flag.description(), deserializer, sink,
          shortSwitch, longSwitch, flag.help(), flag.version());
    }
    if (parameterAnnotation instanceof OptionParameter option) {
      ShortSwitchNameCoordinate shortSwitch = Optional.of(option.shortName())
          .filter(not(String::isEmpty)).map(ShortSwitchNameCoordinate::fromString).orElse(null);
      LongSwitchNameCoordinate longSwitch = Optional.of(option.longName())
          .filter(not(String::isEmpty)).map(LongSwitchNameCoordinate::fromString).orElse(null);
      if (shortSwitch == null && longSwitch == null) {
        throw new NoNameConfigurationException(name);
      }
      return new OptionConfigurationParameter(name, option.description(), option.required(),
          deserializer, sink, shortSwitch, longSwitch);
    }
    if (parameterAnnotation instanceof PositionalParameter positional) {
      PositionCoordinate position = Optional.of(positional.position()).filter(x -> x >= 0)
          .map(PositionCoordinate::new)
          .orElseThrow(() -> new InvalidPositionConfigurationException(name, positional.position()));
      return new PositionalConfigurationParameter(name, positional.description(),
          positional.required(), deserializer, sink, position);
    }
    if (parameterAnnotation instanceof EnvironmentParameter environment) {
      VariableNameCoordinate variableName = Optional.of(environment.variableName())
          .filter(not(String::isEmpty)).map(VariableNameCoordinate::fromString)
          .orElseThrow(() -> new NoNameConfigurationException(name));
      return new EnvironmentConfigurationParameter(name, environment.description(),
          environment.required(), deserializer, sink, variableName);
    }
    if (parameterAnnotation instanceof PropertyParameter property) {
      PropertyNameCoordinate propertyName = Optional.of(property.propertyName())
          .filter(not(String::isEmpty)).map(PropertyNameCoordinate::fromString)
          .orElseThrow(() -> new NoNameConfigurationException(name));
      return new PropertyConfigurationParameter(name, property.description(), property.required(),
          deserializer, sink, propertyName);
    }
    throw new IllegalArgumentException("unexpected annotation " + parameterAnnotation);
  }
}
