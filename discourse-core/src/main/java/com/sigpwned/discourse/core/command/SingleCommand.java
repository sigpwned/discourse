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

import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.model.coordinate.Coordinate;
import com.sigpwned.discourse.core.model.coordinate.LongSwitchNameCoordinate;
import com.sigpwned.discourse.core.model.coordinate.PositionCoordinate;
import com.sigpwned.discourse.core.model.coordinate.ShortSwitchNameCoordinate;
import com.sigpwned.discourse.core.exception.configuration.DuplicateCoordinateConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.InvalidCollectionParameterPlacementConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.InvalidRequiredParameterPlacementConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.MissingPositionConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.MultipleHelpFlagsConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.MultipleVersionFlagsConfigurationException;
import com.sigpwned.discourse.core.parameter.ConfigurationParameter;
import com.sigpwned.discourse.core.parameter.FlagConfigurationParameter;
import com.sigpwned.discourse.core.parameter.PositionalConfigurationParameter;
import com.sigpwned.discourse.core.util.Streams;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * <p>
 * A single command is a simple, standalone command that simply takes arguments. For example, the
 * following command has two parameters, {@code --foo} and {@code --bar}:
 * </p>
 *
 * <pre>
 *   &#x40;Configurable
 *   public class MyCommand {
 *     &#x40;OptionParameter(longName = "foo")
 *     public String foo;
 *
 *     &#x40;OptionParameter(longName = "bar")
 *     public String bar;
 *   }
 * </pre>
 *
 * <p>
 * Note that the {@code MyCommand} class is not abstract. All single command classes must be
 * concrete.
 * </p>
 *
 * @param <T>
 */
public final class SingleCommand<T> extends Command<T> {

  public static interface InstanceFactory<T> {

    public Class<T> getRawType();

    public List<ConfigurationParameter> getParameters();

    public T createInstance(Map<String, Object> arguments);
  }

  // TODO Raw type instance field?
  private final InstanceFactory<T> factory;

  public SingleCommand(String name, String description, String version,
      InstanceFactory<T> factory) {
    super(name, description, version);
    this.factory = requireNonNull(factory);

    // We should not define the same shortName for multiple parameters
    List<ShortSwitchNameCoordinate> shortNames = getParameters().stream()
        .flatMap(p -> p.getCoordinates().stream())
        .mapMulti(Streams.filterAndCast(ShortSwitchNameCoordinate.class)).toList();
    if (Streams.duplicates(shortNames.stream()).findAny().isPresent()) {
      throw new DuplicateCoordinateConfigurationException(
          Streams.duplicates(shortNames.stream()).findFirst().orElseThrow());
    }

    // We should not define the same longName for multiple parameters
    List<LongSwitchNameCoordinate> longNames = getParameters().stream()
        .flatMap(p -> p.getCoordinates().stream())
        .mapMulti(Streams.filterAndCast(LongSwitchNameCoordinate.class)).toList();
    if (Streams.duplicates(longNames.stream()).findAny().isPresent()) {
      throw new DuplicateCoordinateConfigurationException(
          Streams.duplicates(longNames.stream()).findFirst().orElseThrow());
    }

    // We should not define multiple help flags
    if (getParameters().stream().mapMulti(Streams.filterAndCast(FlagConfigurationParameter.class))
        .filter(FlagConfigurationParameter::isHelp).count() > 1) {
      throw new MultipleHelpFlagsConfigurationException(getRawType());
    }

    // We should not define multiple version flags
    if (getParameters().stream().mapMulti(Streams.filterAndCast(FlagConfigurationParameter.class))
        .filter(FlagConfigurationParameter::isVersion).count() > 1) {
      throw new MultipleVersionFlagsConfigurationException(getRawType());
    }

    // We should define valid positional parameters
    boolean optional = false;
    List<PositionalConfigurationParameter> positionalParameters = getParameters().stream()
        .mapMulti(Streams.filterAndCast(PositionalConfigurationParameter.class))
        .sorted(Comparator.comparing(PositionalConfigurationParameter::getPosition)).toList();
    for (int i = 0; i < positionalParameters.size(); i++) {
      PositionalConfigurationParameter parameter = positionalParameters.get(i);
      PositionCoordinate coordinate = parameter.getPosition();
      int position = coordinate.getIndex();

      if (position > i) {
        if (i == 0) {
          throw new MissingPositionConfigurationException(0);
        } else {
          throw new MissingPositionConfigurationException(
              positionalParameters.get(i - 1).getPosition().getIndex() + 1);
        }
      } else if (position < i) {
        throw new DuplicateCoordinateConfigurationException(coordinate);
      }

      if (parameter.isCollection() && i < positionalParameters.size() - 1) {
        // If the parameter is a collection, it must be the last positional parameter. Otherwise,
        // it would "eat" parameters that should be consumed by subsequent positional parameters.
        throw new InvalidCollectionParameterPlacementConfigurationException(i);
      }

      if (optional && parameter.isRequired()) {
        throw new InvalidRequiredParameterPlacementConfigurationException(i);
      }

      if (!parameter.isRequired()) {
        optional = true;
      }
    }
  }

  public Class<T> getRawType() {
    return getInstanceFactory().getRawType();
  }

  public Optional<FlagConfigurationParameter> findHelpFlag() {
    // We know there will be exactly 0 or exactly 1 help flag because of constructor validation
    return getParameters().stream()
        .mapMulti(Streams.filterAndCast(FlagConfigurationParameter.class))
        .filter(FlagConfigurationParameter::isHelp).findFirst();
  }

  public Optional<FlagConfigurationParameter> findVersionFlag() {
    // We know there will be exactly 0 or exactly 1 version flag because of constructor validation
    return getParameters().stream()
        .mapMulti(Streams.filterAndCast(FlagConfigurationParameter.class))
        .filter(FlagConfigurationParameter::isVersion).findFirst();
  }

  public Set<ConfigurationParameter> getParameters() {
    return new HashSet<>(getInstanceFactory().getParameters());
  }

  public Optional<ConfigurationParameter> findParameter(Coordinate coordinate) {
    return getParameters().stream().filter(p -> p.getCoordinates().contains(coordinate))
        .findFirst();
  }

  public InstanceFactory<T> getInstanceFactory() {
    return factory;
  }
}
