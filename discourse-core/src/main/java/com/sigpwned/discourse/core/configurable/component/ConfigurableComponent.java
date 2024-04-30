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

import com.sigpwned.discourse.core.annotation.Configurable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

/**
 * A {@code ConfigurableComponent} is a code element (e.g., field, method, etc.) in a
 * {@link Configurable @Configurable}-annotated class that is related to a logical attribute.
 */
public abstract sealed class ConfigurableComponent permits FieldConfigurableComponent,
    GetterConfigurableComponent, InputConfigurableComponent, SetterConfigurableComponent {

  private final String name;
  private final Class<?> rawType;
  private final Type genericType;
  private final List<Annotation> annotations;

  public ConfigurableComponent(String name, Class<?> rawType, Type genericType,
      List<Annotation> annotations) {
    this.name = requireNonNull(name);
    this.rawType = requireNonNull(rawType);
    this.genericType = requireNonNull(genericType);
    this.annotations = unmodifiableList(annotations);
  }

  public String getName() {
    return name;
  }

  public Class<?> getRawType() {
    return rawType;
  }

  public Type getGenericType() {
    return genericType;
  }

  public List<Annotation> getAnnotations() {
    return annotations;
  }
}
