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

import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.configurable.component.ConfigurableComponent;
import com.sigpwned.discourse.core.configurable.component.SetterConfigurableComponent;
import com.sigpwned.discourse.core.util.ClassWalkers;
import com.sigpwned.discourse.core.util.Streams;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

/**
 * A {@link ConfigurableComponentScanner} that scans for instance setter {@link Method methods}. An
 * instance setter method is a method that takes exactly one argument, returns void, and is not
 * static. This scanner does not care about a method's name or visibility.
 */
public class SetterConfigurableComponentScanner implements ConfigurableComponentScanner {

  public static SetterConfigurableComponentScanner INSTANCE = new SetterConfigurableComponentScanner();

  @Override
  public List<ConfigurableComponent> scanForComponents(Class<?> rawType,
      InvocationContext context) {
    return ClassWalkers.streamClassAndSuperclasses(rawType)
        .mapMulti(Streams.filterAndCast(Method.class)).filter(
            method -> method.getParameterCount() == 1 && void.class.equals(method.getReturnType())
                && !Modifier.isStatic(method.getModifiers()))
        .<ConfigurableComponent>map(SetterConfigurableComponent::new).toList();
  }
}
