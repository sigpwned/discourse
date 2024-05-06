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
package com.sigpwned.discourse.core.configurable.component.scanner;

import com.sigpwned.discourse.core.configurable.component.ConfigurableComponent;
import com.sigpwned.discourse.core.configurable.component.GetterConfigurableComponent;
import com.sigpwned.discourse.core.util.ClassWalkers;
import com.sigpwned.discourse.core.util.Streams;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

/**
 * A {@link ConfigurableComponentScanner} that scans for instance getter {@link Method methods}. An
 * instance getter method is a method that takes no arguments, returns a value, and is not static.
 * This scanner does not care about a method's name or visibility.
 */
public class GetterConfigurableComponentScanner implements ConfigurableComponentScanner {

  public static final GetterConfigurableComponentScanner INSTANCE = new GetterConfigurableComponentScanner();

  @Override
  public List<ConfigurableComponent> scanForComponents(Class<?> rawType) {
    return ClassWalkers.streamClassAndSuperclasses(rawType)
        .mapMulti(Streams.filterAndCast(Method.class)).filter(
            method -> method.getParameterCount() == 0 && !void.class.equals(method.getReturnType())
                && !Modifier.isStatic(method.getModifiers()))
        .<ConfigurableComponent>map(GetterConfigurableComponent::new).toList();
  }
}
