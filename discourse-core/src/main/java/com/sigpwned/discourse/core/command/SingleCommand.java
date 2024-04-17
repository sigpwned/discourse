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

import static java.util.stream.Collectors.toSet;

import com.sigpwned.discourse.core.ConfigurationClass;
import com.sigpwned.discourse.core.Invocation;
import com.sigpwned.discourse.core.SerializationContext;
import com.sigpwned.discourse.core.SinkContext;
import com.sigpwned.discourse.core.annotation.Configurable;
import com.sigpwned.discourse.core.exception.configuration.NotConfigurableConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.UnexpectedDiscriminatorConfigurationException;
import com.sigpwned.discourse.core.exception.configuration.UnexpectedSubcommandsConfigurationException;
import com.sigpwned.discourse.core.parameter.ConfigurationParameter;
import java.util.List;
import java.util.Set;

public final class SingleCommand<T> extends Command<T> {

  public static <T> SingleCommand<T> scan(SinkContext storage, SerializationContext serialization,
      Class<T> rawType) {
    Configurable configurable = rawType.getAnnotation(Configurable.class);

    if (configurable == null) {
      throw new NotConfigurableConfigurationException(rawType);
    }
    if (!configurable.discriminator().isEmpty()) {
      throw new UnexpectedDiscriminatorConfigurationException(rawType);
    }
    if (configurable.subcommands().length != 0) {
      throw new UnexpectedSubcommandsConfigurationException(rawType);
    }

    ConfigurationClass configurationClass = ConfigurationClass.scan(storage, serialization,
        rawType);

    return new SingleCommand<>(configurationClass);
  }

  private final ConfigurationClass configurationClass;

  public SingleCommand(ConfigurationClass configurationClass) {
    if (configurationClass == null) {
      throw new NullPointerException();
    }
    this.configurationClass = configurationClass;
  }

  /**
   * @return the configurationClass
   */
  public ConfigurationClass getConfigurationClass() {
    return configurationClass;
  }

  public Invocation<T> args(List<String> args) {
    return newInvocation(getConfigurationClass(), args);
  }

  /**
   * extension hook factory method
   */
  protected Invocation<T> newInvocation(ConfigurationClass configurationClass, List<String> args) {
    return new Invocation<>(this, getConfigurationClass(), args);
  }

  @Override
  public String getName() {
    return getConfigurationClass().getName();
  }

  @Override
  public String getDescription() {
    return getConfigurationClass().getDescription();
  }

  @Override
  public String getVersion() {
    return getConfigurationClass().getVersion();
  }

  @Override
  public Set<ConfigurationParameter> getParameters() {
    return getConfigurationClass().getParameters().stream().distinct().collect(toSet());
  }
}
