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

import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.configurable.ConfigurableComponent;
import com.sigpwned.discourse.core.configurable.component.element.ConfigurableElement;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

/**
 * A {@link ConfigurableComponent} that is backed by a getter method.
 */
public final class GetterConfigurableComponent implements ConfigurableComponent {

  private final Method method;
  private final ConfigurableElement element;

  public GetterConfigurableComponent(String name, Method method) {
    this.method = requireNonNull(method);
    if (method.getParameterCount() != 0) {
      throw new IllegalArgumentException("method must not have parameters");
    }
    if (method.getReturnType() == void.class) {
      throw new IllegalArgumentException("method must have a non-void return type");
    }
    if (Modifier.isStatic(method.getModifiers())) {
      throw new IllegalArgumentException("method must not be static");
    }

    this.element = new ConfigurableElement() {
      private final List<Annotation> annotations = List.of(
          method.getAnnotatedReturnType().getAnnotations());

      @Override
      public String getName() {
        return name;
      }

      @Override
      public Type getGenericType() {
        return method.getGenericReturnType();
      }

      @Override
      public List<Annotation> getAnnotations() {
        return annotations;
      }
    };
  }

  @Override
  public Method getCodeObject() {
    return getMethod();
  }

  @Override
  public List<Annotation> getAnnotations() {
    return List.of(getMethod().getAnnotations());
  }

  public List<ConfigurableElement> getSinks() {
    return List.of();
  }

  public Optional<ConfigurableElement> getSource() {
    return Optional.of(element);
  }

  private Method getMethod() {
    return method;
  }
}
