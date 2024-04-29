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

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.List;

/**
 * A {@link ConfigurableComponent} that represents an input parameter to the object Configurable
 * class' constructor.
 */
public final class InputConfigurableComponent extends ConfigurableComponent {

  private final int index;
  private final Parameter parameter;

  public InputConfigurableComponent(int index, Parameter parameter, Type genericType) {
    super(parameter.getName(), parameter.getType(), genericType,
        List.of(parameter.getAnnotations()));
    if (index < 0) {
      throw new IllegalArgumentException("index must not be negative");
    }
    this.index = index;
    this.parameter = requireNonNull(parameter);
  }

  public int getIndex() {
    return index;
  }

  public Parameter getParameter() {
    return parameter;
  }
}
