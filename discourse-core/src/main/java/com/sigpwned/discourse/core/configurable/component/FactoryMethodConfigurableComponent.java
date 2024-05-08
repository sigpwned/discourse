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
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * A {@link ConfigurableComponent} that is backed by a constructor.
 */
public final class FactoryMethodConfigurableComponent implements ConfigurableComponent {

  private final Method method;
  private final List<ConfigurableElement> parameters;
  private final ConfigurableElement result;

  public FactoryMethodConfigurableComponent(Method method) {
    this.method = requireNonNull(method);
    this.result = new ConfigurableElement() {
      @Override
      public String getName() {
        return ConfigurableElement.INSTANCE_NAME;
      }

      @Override
      public Type getGenericType() {
        return method.getDeclaringClass();
      }

      @Override
      public List<Annotation> getAnnotations() {
        return List.of(method.getAnnotatedReturnType().getAnnotations());
      }
    };
    this.parameters = IntStream.range(0, getMethod().getParameterCount()).mapToObj(i -> {
      final Parameter parameter = getMethod().getParameters()[i];
      final Type genericType = parameter.getParameterizedType();
      return (ConfigurableElement) new ConfigurableElement() {
        @Override
        public String getName() {
          // TODO Uh-oh...
          return parameter.getName();
        }

        @Override
        public Type getGenericType() {
          return genericType;
        }

        @Override
        public List<Annotation> getAnnotations() {
          return List.of(parameter.getAnnotations());
        }
      };
    }).toList();
  }

  @Override
  public Method getCodeObject() {
    return getMethod();
  }

  @Override
  public List<Annotation> getAnnotations() {
    return List.of(getMethod().getAnnotations());
  }

  @Override
  public List<ConfigurableElement> getSinks() {
    return parameters;
  }

  @Override
  public Optional<ConfigurableElement> getSource() {
    return Optional.of(result);
  }

  private Method getMethod() {
    return method;
  }
}
