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

import com.sigpwned.discourse.core.ConfigurationClass;
import com.sigpwned.discourse.core.Invocation;
import com.sigpwned.discourse.core.SerializationContext;
import com.sigpwned.discourse.core.SinkContext;
import com.sigpwned.discourse.core.exception.configuration.UnexpectedDiscriminatorConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.UnexpectedSubcommandsConfigurationException;
import com.sigpwned.discourse.core.parameter.ConfigurationParameter;
import com.sigpwned.espresso.BeanInstance;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public final class SingleCommand<T> extends Command<T> {

  private final Set<ConfigurationParameter> parameters;

  public static <T> SingleCommand<T> scan(SinkContext storage, SerializationContext serialization,
      ConfigurationClass configurationClass) {
    if (configurationClass.getDiscriminator().isPresent()) {
      throw new UnexpectedDiscriminatorConfigurationException(configurationClass.getRawType());
    }
    if (!configurationClass.getSubcommands().isEmpty()) {
      throw new UnexpectedSubcommandsConfigurationException(configurationClass.getRawType());
    }

    return new SingleCommand<>(configurationClass.getName(), configurationClass.getDescription(),
        configurationClass.getVersion(), new HashSet<>(configurationClass.getParameters()),
        configurationClass::newInstance);
  }

  private final Supplier<BeanInstance> beanSupplier;

  public SingleCommand(String name, String description, String version,
      Set<ConfigurationParameter> parameters, Supplier<BeanInstance> beanSupplier) {
    super(name, description, version);
    this.parameters = requireNonNull(parameters);
    this.beanSupplier = requireNonNull(beanSupplier);
  }

  public Invocation<T> args(List<String> args) {
    return new Invocation<>(this, getBeanSupplier(), args);
  }

  @Override
  public Set<ConfigurationParameter> getParameters() {
    return parameters;
  }

  private Supplier<BeanInstance> getBeanSupplier() {
    return beanSupplier;
  }
}
