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

import com.sigpwned.discourse.core.configurable.component.element.ConfigurableElement;
import com.sigpwned.discourse.core.configurable.component.element.ConstructorConfigurableElement;
import com.sigpwned.discourse.core.configurable.component.element.InputConfigurableElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * A {@link ConfigurableComponent} that is backed by a constructor.
 */
public final class ConstructorConfigurableComponent implements ConfigurableComponent {

  private final Constructor<?> constructor;
  private final ConstructorConfigurableElement instanceElement;
  private final List<InputConfigurableElement> inputElements;

  public ConstructorConfigurableComponent(Constructor<?> constructor) {
    this.constructor = requireNonNull(constructor);
    this.instanceElement = new ConstructorConfigurableElement(constructor);
    this.inputElements = IntStream.range(0, getConstructor().getParameterCount()).mapToObj(
        i -> new InputConfigurableElement(i, getConstructor().getParameters()[i],
            getConstructor().getGenericParameterTypes()[i])).toList();
    if (!Modifier.isPublic(constructor.getModifiers())) {
      throw new IllegalArgumentException("constructor must be public");
    }
  }

  @Override
  public Class<?> getDeclaringClass() {
    return getConstructor().getDeclaringClass();
  }

  @Override
  public Constructor<?> getAccessibleObject() {
    return getConstructor();
  }

  @Override
  public List<ConfigurableElement> getElements() {
    List<ConfigurableElement> result = new ArrayList<>();
    result.add(getInstanceElement());
    result.addAll(getInputElements());
    return unmodifiableList(result);
  }

  public ConstructorConfigurableElement getInstanceElement() {
    return instanceElement;
  }

  public List<InputConfigurableElement> getInputElements() {
    return inputElements;
  }

  @Override
  public boolean isSink() {
    return true;
  }

  private Constructor<?> getConstructor() {
    return constructor;
  }
}
