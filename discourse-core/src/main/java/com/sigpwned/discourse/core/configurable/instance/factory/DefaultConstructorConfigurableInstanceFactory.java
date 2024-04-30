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
package com.sigpwned.discourse.core.configurable.instance.factory;

import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.configurable.component.InputConfigurableComponent;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * A {@link ConfigurableInstanceFactory} that uses a class' default constructor to create
 * instances.
 */
public class DefaultConstructorConfigurableInstanceFactory<T> implements
    ConfigurableInstanceFactory<T> {

  public static class Provider implements ConfigurableInstanceFactoryScanner {

    @Override
    public <T> Optional<ConfigurableInstanceFactory<T>> scanForInstanceFactory(Class<T> type) {
      Constructor<T> defaultConstructor;
      try {
        defaultConstructor = type.getConstructor();
      } catch (NoSuchMethodException e) {
        return Optional.empty();
      }
      if (!Modifier.isPublic(defaultConstructor.getModifiers())) {
        return Optional.empty();
      }
      return Optional.of(new DefaultConstructorConfigurableInstanceFactory<>(defaultConstructor));
    }
  }

  private final Constructor<T> defaultConstructor;

  public DefaultConstructorConfigurableInstanceFactory(Constructor<T> defaultConstructor) {
    this.defaultConstructor = requireNonNull(defaultConstructor);
    if (defaultConstructor.getParameterCount() != 0) {
      throw new IllegalArgumentException("Default constructor must have no parameters");
    }
    if (!Modifier.isPublic(defaultConstructor.getModifiers())) {
      throw new IllegalArgumentException("Default constructor must be accessible");
    }
  }

  @Override
  public List<InputConfigurableComponent> getInputs() {
    return List.of();
  }

  @Override
  public T createInstance(Map<String, Object> arguments) {
    try {
      return defaultConstructor.newInstance();
    } catch (ReflectiveOperationException e) {
      // TODO better exception?
      throw new RuntimeException(e);
    }
  }
}
