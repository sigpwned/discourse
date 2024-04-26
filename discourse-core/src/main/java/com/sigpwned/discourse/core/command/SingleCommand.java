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

import com.sigpwned.discourse.core.coordinate.Coordinate;
import com.sigpwned.discourse.core.parameter.ConfigurationParameter;
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
  }

  public Class<T> getRawType() {
    return getInstanceFactory().getRawType();
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
