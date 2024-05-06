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
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

/**
 * A {@link ConfigurableElement} that is backed by a field.
 */
public final class FieldConfigurableElement implements ConfigurableElement {

  private final Field field;

  public FieldConfigurableElement(Field field) {
    this.field = requireNonNull(field);
  }

  public boolean isVisible() {
    return Modifier.isPublic(getField().getModifiers());
  }

  public boolean isMutable() {
    return !Modifier.isFinal(getField().getModifiers());
  }

  @Override
  public String getName() {
    return getField().getName();
  }

  @Override
  public Type getGenericType() {
    return getField().getGenericType();
  }

  @Override
  public List<Annotation> getAnnotations() {
    return List.of(getField().getAnnotations());
  }

  private Field getField() {
    return field;
  }

  /**
   * Returns a setter for the component, if it is visible and mutable.
   *
   * @return a setter for the component, if it is visible and mutable
   * @throws RuntimeException if there was an error setting the field
   */
  public Optional<BiConsumer<Object, Object>> getSetter() {
    if (isVisible() && isMutable()) {
      return Optional.of((object, value) -> {
        try {
          getField().set(object, value);
        } catch (IllegalAccessException e) {
          // TODO better exception?
          throw new RuntimeException("Failed to set field", e);
        }
      });
    }
    return Optional.empty();
  }
}