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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

public final class SetterConfigurableComponent extends ConfigurableComponent {

  private final Method method;

  public SetterConfigurableComponent(Method method) {
    super(method.getName(), method.getParameterTypes()[0], method.getGenericParameterTypes()[0],
        List.of(method.getAnnotations()));
    this.method = requireNonNull(method);
  }

  public boolean isVisible() {
    return Modifier.isPublic(getMethod().getModifiers());
  }

  public Method getMethod() {
    return method;
  }

  /**
   * Returns a setter for the component, if it is visible.
   *
   * @return a setter for the component, if it is visible
   * @throws RuntimeException if there was an error invoking the setter
   */
  public Optional<BiConsumer<Object, Object>> getSetter() {
    if (isVisible()) {
      return Optional.of((object, value) -> {
        try {
          getMethod().invoke(object, value);
        } catch (ReflectiveOperationException e) {
          // TODO better exception?
          throw new RuntimeException("Failed to invoke setter", e);
        }
      });
    }
    return Optional.empty();
  }
}
