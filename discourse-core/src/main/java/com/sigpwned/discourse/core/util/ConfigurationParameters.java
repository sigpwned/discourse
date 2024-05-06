/*-
 * =================================LICENSE_START==================================
 * discourse-core
 * ====================================SECTION=====================================
 * Copyright (C) 2022 - 2024 Andy Boothe
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
package com.sigpwned.discourse.core.util;

import com.sigpwned.discourse.core.annotation.EnvironmentParameter;
import com.sigpwned.discourse.core.annotation.FlagParameter;
import com.sigpwned.discourse.core.annotation.OptionParameter;
import com.sigpwned.discourse.core.annotation.PositionalParameter;
import com.sigpwned.discourse.core.annotation.PropertyParameter;
import com.sigpwned.discourse.core.model.coordinate.LongSwitchNameCoordinate;
import com.sigpwned.discourse.core.model.coordinate.PositionCoordinate;
import com.sigpwned.discourse.core.model.coordinate.PropertyNameCoordinate;
import com.sigpwned.discourse.core.model.coordinate.ShortSwitchNameCoordinate;
import com.sigpwned.discourse.core.model.coordinate.VariableNameCoordinate;
import com.sigpwned.discourse.core.exception.configuration.InvalidLongNameConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.InvalidPositionConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.InvalidPropertyNameConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.InvalidShortNameConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.InvalidVariableNameConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.NoNameConfigurationException;
import com.sigpwned.discourse.core.parameter.ConfigurationParameter;
import com.sigpwned.discourse.core.parameter.EnvironmentConfigurationParameter;
import com.sigpwned.discourse.core.parameter.FlagConfigurationParameter;
import com.sigpwned.discourse.core.parameter.OptionConfigurationParameter;
import com.sigpwned.discourse.core.parameter.PositionalConfigurationParameter;
import com.sigpwned.discourse.core.parameter.PropertyConfigurationParameter;
import com.sigpwned.discourse.core.value.deserializer.ValueDeserializer;
import com.sigpwned.discourse.core.value.sink.ValueSink;
import java.lang.annotation.Annotation;

public final class ConfigurationParameters {

  private ConfigurationParameters() {
  }

  public static ConfigurationParameter createConfigurationParameter(Annotation parameterAnnotation,
      String name, ValueDeserializer<?> deserializer, ValueSink sink) {
    if (parameterAnnotation instanceof FlagParameter flag) {
      ShortSwitchNameCoordinate shortSwitch;
      try {
        shortSwitch = MoreAnnotations.getString(flag, FlagParameter::shortName)
            .map(ShortSwitchNameCoordinate::fromString).orElse(null);
      } catch (IllegalArgumentException e) {
        throw new InvalidShortNameConfigurationException(flag.shortName());
      }

      LongSwitchNameCoordinate longSwitch;
      try {
        longSwitch = MoreAnnotations.getString(flag, FlagParameter::longName)
            .map(LongSwitchNameCoordinate::fromString).orElse(null);
      } catch (IllegalArgumentException e) {
        throw new InvalidLongNameConfigurationException(flag.longName());
      }

      if (shortSwitch == null && longSwitch == null) {
        throw new NoNameConfigurationException(name);
      }

      String description = MoreAnnotations.getString(flag, FlagParameter::description).orElse(null);

      return new FlagConfigurationParameter(name, description, deserializer, sink, shortSwitch,
          longSwitch, flag.help(), flag.version());
    }
    if (parameterAnnotation instanceof OptionParameter option) {
      ShortSwitchNameCoordinate shortSwitch;
      try {
        shortSwitch = MoreAnnotations.getString(option, OptionParameter::shortName)
            .map(ShortSwitchNameCoordinate::fromString).orElse(null);
      } catch (IllegalArgumentException e) {
        throw new InvalidShortNameConfigurationException(option.shortName());
      }

      LongSwitchNameCoordinate longSwitch;
      try {
        longSwitch = MoreAnnotations.getString(option, OptionParameter::longName)
            .map(LongSwitchNameCoordinate::fromString).orElse(null);
      } catch (IllegalArgumentException e) {
        throw new InvalidLongNameConfigurationException(option.longName());
      }

      if (shortSwitch == null && longSwitch == null) {
        throw new NoNameConfigurationException(name);
      }

      String description = MoreAnnotations.getString(option, OptionParameter::description)
          .orElse(null);

      return new OptionConfigurationParameter(name, description, option.required(), deserializer,
          sink, shortSwitch, longSwitch);
    }
    if (parameterAnnotation instanceof PositionalParameter positional) {
      PositionCoordinate position;
      try {
        position = MoreAnnotations.getInt(positional, PositionalParameter::position).stream()
            .mapToObj(PositionCoordinate::of).findFirst().orElse(null);
      } catch (IllegalArgumentException e) {
        throw new InvalidPositionConfigurationException(name, positional.position());
      }

      if (position == null) {
        // TODO better exception?
        throw new NoNameConfigurationException(name);
      }

      String description = MoreAnnotations.getString(positional, PositionalParameter::description)
          .orElse(null);

      return new PositionalConfigurationParameter(name, description, positional.required(),
          deserializer, sink, position);
    }
    if (parameterAnnotation instanceof EnvironmentParameter environment) {
      VariableNameCoordinate variableName;
      try {
        variableName = MoreAnnotations.getString(environment, EnvironmentParameter::variableName)
            .map(VariableNameCoordinate::fromString).orElse(null);
      } catch (IllegalArgumentException e) {
        throw new InvalidVariableNameConfigurationException(name);
      }

      if (variableName == null) {
        throw new NoNameConfigurationException(name);
      }

      String description = MoreAnnotations.getString(environment, EnvironmentParameter::description)
          .orElse(null);

      return new EnvironmentConfigurationParameter(name, description, environment.required(),
          deserializer, sink, variableName);
    }
    if (parameterAnnotation instanceof PropertyParameter property) {
      PropertyNameCoordinate propertyName;
      try {
        propertyName = MoreAnnotations.getString(property, PropertyParameter::propertyName)
            .map(PropertyNameCoordinate::fromString).orElse(null);
      } catch (IllegalArgumentException e) {
        throw new InvalidPropertyNameConfigurationException(name);
      }

      if (propertyName == null) {
        throw new NoNameConfigurationException(name);
      }

      String description = MoreAnnotations.getString(property, PropertyParameter::description)
          .orElse(null);

      return new PropertyConfigurationParameter(name, description, property.required(),
          deserializer, sink, propertyName);
    }
    throw new IllegalArgumentException("unexpected annotation " + parameterAnnotation);
  }
}
