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
import com.sigpwned.discourse.core.configurable.component.element.FactoryMethodConfigurableElement;
import com.sigpwned.discourse.core.configurable.component.element.InputConfigurableElement;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * A {@link ConfigurableComponent} that is backed by a constructor.
 */
public final class FactoryMethodConfigurableComponent implements ConfigurableComponent {

  private final Method factoryMethod;
  private final FactoryMethodConfigurableElement instanceElement;
  private final List<InputConfigurableElement> inputElements;

  public FactoryMethodConfigurableComponent(Method factoryMethod) {
    this.factoryMethod = requireNonNull(factoryMethod);
    this.instanceElement = new FactoryMethodConfigurableElement(factoryMethod);
    this.inputElements = IntStream.range(0, getFactoryMethod().getParameterCount()).mapToObj(
        i -> new InputConfigurableElement(i, getFactoryMethod().getParameters()[i],
            getFactoryMethod().getGenericParameterTypes()[i])).toList();
    if (!Modifier.isPublic(factoryMethod.getModifiers())) {
      throw new IllegalArgumentException("factory method must be public");
    }
    if (!Modifier.isStatic(factoryMethod.getModifiers())) {
      throw new IllegalArgumentException("factory method must be static");
    }
    if (factoryMethod.getReturnType() == void.class) {
      throw new IllegalArgumentException("factory method must have a non-void return type");
    }
    if (!factoryMethod.getReturnType().isAssignableFrom(getDeclaringClass())) {
      throw new IllegalArgumentException(
          "factory method must return a type assignable to the declaring class");
    }
  }

  @Override
  public Class<?> getDeclaringClass() {
    return getFactoryMethod().getDeclaringClass();
  }

  @Override
  public Method getAccessibleObject() {
    return getFactoryMethod();
  }

  @Override
  public List<ConfigurableElement> getElements() {
    List<ConfigurableElement> elements = new ArrayList<>();
    elements.add(getInstanceElement());
    elements.addAll(getInputElements());
    return elements;
  }

  public FactoryMethodConfigurableElement getInstanceElement() {
    return instanceElement;
  }

  public List<InputConfigurableElement> getInputElements() {
    return inputElements;
  }

  @Override
  public boolean isSink() {
    return true;
  }

  private Method getFactoryMethod() {
    return factoryMethod;
  }
}
