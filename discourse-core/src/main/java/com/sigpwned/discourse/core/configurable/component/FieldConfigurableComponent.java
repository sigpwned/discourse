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
import com.sigpwned.discourse.core.configurable.ConfigurableSink;
import com.sigpwned.discourse.core.configurable.ConfigurableSource;
import com.sigpwned.discourse.core.configurable.component.element.ConfigurableElement;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

/**
 * A {@link ConfigurableComponent} that is backed by a field.
 */
public final class FieldConfigurableComponent implements ConfigurableComponent {

  private final Field field;
  private final ConfigurableSource source;
  private final ConfigurableSink sink;

  public FieldConfigurableComponent(String name, Field field) {
    if (name == null) {
      throw new NullPointerException();
    }
    this.field = requireNonNull(field);
    this.element = new ConfigurableElement() {
      @Override
      public String getName() {
        return name;
      }

      @Override
      public Type getGenericType() {
        return getField().getGenericType();
      }

      @Override
      public List<Annotation> getAnnotations() {
        return List.of(getField().getAnnotations());
      }
    };
  }

  @Override
  public Field getCodeObject() {
    return getField();
  }

  @Override
  public List<Annotation> getAnnotations() {
    return List.of(getField().getAnnotations());
  }

  @Override
  public List<ConfigurableElement> getSinks() {
    return List.of(getElement());
  }

  @Override
  public Optional<ConfigurableElement> getSource() {
    return Optional.of(getElement());
  }

  private Field getField() {
    return field;
  }

  private ConfigurableElement getElement() {
    return element;
  }
}
