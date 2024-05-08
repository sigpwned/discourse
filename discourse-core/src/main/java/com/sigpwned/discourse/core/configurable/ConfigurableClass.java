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
package com.sigpwned.discourse.core.configurable;

import static java.util.Collections.*;
import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.annotation.Configurable;
import com.sigpwned.discourse.core.configurable.instance.factory.ConfigurableInstanceFactory;
import java.util.List;
import java.util.stream.Stream;

/**
 * <p>
 * A logical representation of a {@link Configurable @Configuable}-annotated class. It contains the
 * class itself, its {@link ConfigurableInstanceFactory instance factory}, and the
 * {@link ConfigurableComponent components} that make up the instance.
 * </p>
 *
 * <p>
 * Note that the instance factory may also define components, which are not included in
 * {@link #getInstanceComponents()} but are included in {@link #getComponents()}. This allows for
 * instance factories to define components that are not part of the instance itself.
 * </p>
 */
public class ConfigurableClass<T> {

  private final Class<T> clazz;
  private final ConfigurableInstanceFactory<T> instanceFactory;
  private final List<ConfigurableComponent> instanceComponents;

  public ConfigurableClass(Class<T> clazz, ConfigurableInstanceFactory<T> instanceFactory,
      List<ConfigurableComponent> instanceComponents) {
    this.clazz = requireNonNull(clazz);
    this.instanceFactory = requireNonNull(instanceFactory);
    this.instanceComponents = unmodifiableList(instanceComponents);
  }

  public Class<T> getClazz() {
    return clazz;
  }

  public ConfigurableInstanceFactory<T> getInstanceFactory() {
    return instanceFactory;
  }

  public List<ConfigurableComponent> getInstanceComponents() {
    return instanceComponents;
  }

  public List<ConfigurableComponent> getComponents() {
    return Stream.concat(instanceFactory.getInputs().stream(), instanceComponents.stream())
        .toList();
  }
}
