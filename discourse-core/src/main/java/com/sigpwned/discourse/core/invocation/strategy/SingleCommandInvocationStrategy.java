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
package com.sigpwned.discourse.core.invocation.strategy;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toCollection;

import com.sigpwned.discourse.core.ArgumentsParser;
import com.sigpwned.discourse.core.Invocation;
import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.InvocationStrategy;
import com.sigpwned.discourse.core.command.Command;
import com.sigpwned.discourse.core.command.SingleCommand;
import com.sigpwned.discourse.core.exception.argument.AssignmentFailureArgumentException;
import com.sigpwned.discourse.core.exception.argument.NewInstanceFailureArgumentException;
import com.sigpwned.discourse.core.exception.argument.UnassignedRequiredParametersArgumentException;
import com.sigpwned.discourse.core.invocation.DefaultInvocation;
import com.sigpwned.discourse.core.parameter.ConfigurationParameter;
import com.sigpwned.discourse.core.parameter.EnvironmentConfigurationParameter;
import com.sigpwned.discourse.core.parameter.FlagConfigurationParameter;
import com.sigpwned.discourse.core.parameter.OptionConfigurationParameter;
import com.sigpwned.discourse.core.parameter.PositionalConfigurationParameter;
import com.sigpwned.discourse.core.parameter.PropertyConfigurationParameter;
import com.sigpwned.discourse.core.util.Streams;
import com.sigpwned.espresso.BeanInstance;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class SingleCommandInvocationStrategy implements InvocationStrategy {

  @FunctionalInterface
  /* default */ static interface EnvironmentVariables {

    public Optional<String> get(String name);
  }

  @FunctionalInterface
  /* default */ static interface SystemProperties {

    public Optional<String> get(String name);
  }

  private EnvironmentVariables variables;
  private SystemProperties properties;

  public SingleCommandInvocationStrategy() {
    this.variables = name -> Optional.ofNullable(System.getenv(name));
    this.properties = name -> Optional.ofNullable(System.getProperty(name));
  }

  @Override
  public <T> Invocation<? extends T> invoke(Command<T> command, InvocationContext context, List<String> args) {
    if (!(command instanceof SingleCommand<T> single)) {
      throw new IllegalArgumentException("Command is not a SingleCommand");
    }

    BeanInstance instance;
    try {
      instance = single.getBeanClass().newInstance();
    } catch (InvocationTargetException e) {
      throw new NewInstanceFailureArgumentException(e);
    }

    Set<String> requiredPropertyNames = single.getParameters().stream()
        .filter(ConfigurationParameter::isRequired).map(ConfigurationParameter::getName)
        .collect(toCollection(HashSet::new));

    // Handle CLI arguments
    new ArgumentsParser(single::findParameter, new ArgumentsParser.Handler() {
      @Override
      public void flag(FlagConfigurationParameter property) {
        try {
          property.set(instance.getInstance(), "true");
        } catch (InvocationTargetException e) {
          throw new AssignmentFailureArgumentException(property.getName(), e);
        }
        requiredPropertyNames.remove(property.getName());
      }

      @Override
      public void option(OptionConfigurationParameter property, String text) {
        try {
          property.set(instance.getInstance(), text);
        } catch (InvocationTargetException e) {
          throw new AssignmentFailureArgumentException(property.getName(), e);
        }
        requiredPropertyNames.remove(property.getName());
      }

      @Override
      public void positional(PositionalConfigurationParameter property, String text) {
        try {
          property.set(instance.getInstance(), text);
        } catch (InvocationTargetException e) {
          throw new AssignmentFailureArgumentException(property.getName(), e);
        }
        requiredPropertyNames.remove(property.getName());
      }
    }).parse(args);

    // Handle environment variable arguments
    single.getParameters().stream()
        .mapMulti(Streams.filterAndCast(EnvironmentConfigurationParameter.class))
        .forEach(property -> {
          String variableName = property.getVariableName().toString();
          getVariables().get(variableName).ifPresent(text -> {
            try {
              property.set(instance.getInstance(), text);
            } catch (InvocationTargetException e) {
              throw new AssignmentFailureArgumentException(property.getName(), e);
            }
            requiredPropertyNames.remove(property.getName());
          });
        });

    // Handle system property arguments
    single.getParameters().stream()
        .mapMulti(Streams.filterAndCast(PropertyConfigurationParameter.class)).forEach(property -> {
          String propertyName = property.getPropertyName().toString();
          getProperties().get(propertyName).ifPresent(text -> {
            try {
              property.set(instance.getInstance(), text);
            } catch (InvocationTargetException e) {
              throw new AssignmentFailureArgumentException(property.getName(), e);
            }
            requiredPropertyNames.remove(property.getName());
          });
        });

    // Did we get all the required parameters?
    if (!requiredPropertyNames.isEmpty()) {
      throw new UnassignedRequiredParametersArgumentException(requiredPropertyNames);
    }

    return new DefaultInvocation<>(List.of(), single, args, (T) instance.getInstance());
  }

  private EnvironmentVariables getVariables() {
    return variables;
  }

  /**
   * test hook
   */
  void setVariables(EnvironmentVariables variables) {
    this.variables = requireNonNull(variables);
  }

  private SystemProperties getProperties() {
    return properties;
  }

  /**
   * test hook
   */
  void setProperties(SystemProperties properties) {
    this.properties = requireNonNull(properties);
  }
}
