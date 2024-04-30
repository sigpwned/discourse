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

import com.sigpwned.discourse.core.annotation.Configurable;
import com.sigpwned.discourse.core.configurable.component.InputConfigurableComponent;
import java.util.List;
import java.util.Map;

/**
 * A factory for creating instances of concrete {@link Configurable @Configurable}-annotated class
 * from a set of arguments. This is an abstraction of a class constructor, although it may create
 * instances in other ways, e.g., using factory methods, dynamic proxies, etc.
 *
 * @param <T> the type of object that the factory creates
 */
public interface ConfigurableInstanceFactory<T> {

  /**
   * Returns the set of parameters this factory requires to create an instance. The parameters are
   * provided as a list of {@link InputConfigurableComponent} objects, which includes various and
   * sundry information about the parameter, such as its name, type, default value, etc.
   *
   * @return the required parameter names
   */
  public List<InputConfigurableComponent> getInputs();

  /**
   * Creates an instance of the class using the given arguments. The arguments are provided as a map
   * from parameter names to values.
   *
   * @param arguments the arguments
   * @return the instance
   * @throws RuntimeException if there was an error creating the instance
   */
  public T createInstance(Map<String, Object> arguments);
}
