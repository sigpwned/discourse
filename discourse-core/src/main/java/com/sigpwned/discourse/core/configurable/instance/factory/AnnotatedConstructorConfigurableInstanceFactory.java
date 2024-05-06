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
package com.sigpwned.discourse.core.configurable.instance.factory;

import static java.util.Collections.*;
import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.annotation.DiscourseCreator;
import com.sigpwned.discourse.core.configurable.component.InputConfigurableComponent;
import com.sigpwned.discourse.core.configurable.instance.factory.scanner.ConfigurableInstanceFactoryScanner;
import com.sigpwned.discourse.core.util.collectors.Only;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * A {@link ConfigurableInstanceFactory} that uses an
 * {@link DiscourseCreator @DiscourseCreator}-annotated constructor to create instances.
 */
public class AnnotatedConstructorConfigurableInstanceFactory<T> implements
    ConfigurableInstanceFactory<T> {

  private static final Pattern SYNTHETIC_PARAMETER_NAME_PATTERN = Pattern.compile("arg\\d+");

  private final Constructor<T> constructor;
  private final List<InputConfigurableComponent> inputs;

  public AnnotatedConstructorConfigurableInstanceFactory(Constructor<T> constructor,
      List<InputConfigurableComponent> inputs) {
    this.constructor = requireNonNull(constructor);
    this.inputs = unmodifiableList(inputs);
  }

  @Override
  public T createInstance(Map<String, Object> arguments) {
    // TODO Should these be attribute names, or field names?
    Object[] argumentValues = getInputs().stream().map(InputConfigurableComponent::getName)
        .map(parameterName -> Optional.ofNullable(arguments.get(parameterName)).orElseThrow(() -> {
          // TODO better exception
          return new IllegalArgumentException("missing parameter: " + parameterName);
        })).toArray(Object[]::new);
    try {
      return constructor.newInstance(argumentValues);
    } catch (ReflectiveOperationException e) {
      // TODO better exception
      throw new RuntimeException(e);
    }
  }

  @Override
  public List<InputConfigurableComponent> getInputs() {
    return inputs;
  }
}