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
package com.sigpwned.discourse.core.configurable.component;

import static java.util.Collections.*;
import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.configurable.ConfigurableComponent;
import com.sigpwned.discourse.core.configurable.ConfigurableSink;
import com.sigpwned.discourse.core.configurable.ConfigurableSource;
import com.sigpwned.discourse.core.configurable.component.element.ConfigurableElement;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * A {@link ConfigurableComponent} that is backed by a constructor.
 */
public final class ConstructorConfigurableComponent implements ConfigurableComponent {

  private final Constructor<?> constructor;
  private final List<ConfigurableSink> sinks;
  private final ConfigurableSource source;

  public ConstructorConfigurableComponent(Constructor<?> constructor) {
    this.constructor = requireNonNull(constructor);
    // TODO Is this the right return type?
    this.source = new ConfigurableSource(ConfigurableElement.INSTANCE_NAME,
        constructor.getAnnotatedReturnType().getType(),
        List.of(constructor.getAnnotatedReturnType().getAnnotations()));
    // TODO Where do we get parameter names?
    // TODO Where do we get parameter coordinates?
    this.sinks = IntStream.range(0, getConstructor().getParameterCount()).mapToObj(i -> {
      final Parameter parameter = getConstructor().getParameters()[i];
      final Type genericType = parameter.getParameterizedType();
      return new ConfigurableSink(parameter.getName(), genericType,
          List.of(parameter.getAnnotations()), emptyMap());
    }).toList();
  }

  @Override
  public Constructor<?> getCodeObject() {
    return getConstructor();
  }

  @Override
  public List<Annotation> getAnnotations() {
    return List.of(getConstructor().getAnnotations());
  }

  @Override
  public List<ConfigurableSink> getSinks() {
    return sinks;
  }

  @Override
  public Optional<ConfigurableSource> getSource() {
    return Optional.of(source);
  }

  private Constructor<?> getConstructor() {
    return constructor;
  }
}
