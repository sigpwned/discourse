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
package com.sigpwned.discourse.core.configurable.component.element;

import static java.util.Objects.requireNonNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.List;

/**
 * A {@link ConfigurableElement} that is backed by a setter method.
 */
public final class SetterConfigurableElement implements ConfigurableElement {

  private final Method method;

  public SetterConfigurableElement(Method method) {
    this.method = requireNonNull(method);
    if (method.getParameterCount() != 1) {
      throw new IllegalArgumentException("method must have exactly one parameter");
    }
    if (!method.getReturnType().equals(void.class)) {
      throw new IllegalArgumentException("method must return void");
    }
    if (Modifier.isStatic(method.getModifiers())) {
      throw new IllegalArgumentException("method must not be static");
    }
  }

  public boolean isVisible() {
    return Modifier.isPublic(getMethod().getModifiers());
  }

  @Override
  public String getName() {
    return getMethod().getName();
  }

  @Override
  public Type getGenericType() {
    return getMethod().getGenericParameterTypes()[0];
  }

  @Override
  public List<Annotation> getAnnotations() {
    return List.of(getMethod().getAnnotations());
  }

  private Method getMethod() {
    return method;
  }
}
