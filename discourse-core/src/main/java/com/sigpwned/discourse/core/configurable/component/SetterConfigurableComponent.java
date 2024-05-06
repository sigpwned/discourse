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

import com.sigpwned.discourse.core.configurable.component.element.ConfigurableElement;
import com.sigpwned.discourse.core.configurable.component.element.SetterConfigurableElement;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

/**
 * A {@link ConfigurableComponent} that is backed by a setter method.
 */
public final class SetterConfigurableComponent implements ConfigurableComponent {

  private final Method method;
  private final SetterConfigurableElement element;

  public SetterConfigurableComponent(Method method) {
    this.method = requireNonNull(method);
    this.element = new SetterConfigurableElement(method);
    if (method.getParameterCount() != 1) {
      throw new IllegalArgumentException("method must have exactly one parameter");
    }
    if (method.getReturnType() != void.class) {
      throw new IllegalArgumentException("method must have a void return type");
    }
    if (Modifier.isStatic(method.getModifiers())) {
      throw new IllegalArgumentException("method must not be static");
    }
  }

  @Override
  public Class<?> getDeclaringClass() {
    return getMethod().getDeclaringClass();
  }

  @Override
  public AccessibleObject getAccessibleObject() {
    return getMethod();
  }

  @Override
  public boolean isSink() {
    return Modifier.isPublic(getMethod().getModifiers());
  }

  @Override
  public List<ConfigurableElement> getElements() {
    return List.of(getElement());
  }

  private Method getMethod() {
    return method;
  }

  private SetterConfigurableElement getElement() {
    return element;
  }
}
