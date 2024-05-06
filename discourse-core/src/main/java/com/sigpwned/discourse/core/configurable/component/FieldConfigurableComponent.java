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
import com.sigpwned.discourse.core.configurable.component.element.FieldConfigurableElement;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

/**
 * A {@link ConfigurableComponent} that is backed by a field.
 */
public final class FieldConfigurableComponent implements ConfigurableComponent {

  private final Field field;
  private final FieldConfigurableElement element;

  public FieldConfigurableComponent(Field field) {
    this.field = requireNonNull(field);
    this.element = new FieldConfigurableElement(field);
  }

  @Override
  public Class<?> getDeclaringClass() {
    return getField().getDeclaringClass();
  }

  @Override
  public Field getAccessibleObject() {
    return getField();
  }

  @Override
  public List<ConfigurableElement> getElements() {
    return List.of(getElement());
  }

  @Override
  public boolean isSink() {
    return Modifier.isPublic(getField().getModifiers()) && !Modifier.isFinal(
        getField().getModifiers());
  }

  private Field getField() {
    return field;
  }

  private FieldConfigurableElement getElement() {
    return element;
  }
}
