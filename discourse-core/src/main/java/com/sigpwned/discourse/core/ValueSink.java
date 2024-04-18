/*-
 * =================================LICENSE_START==================================
 * discourse-core
 * ====================================SECTION=====================================
 * Copyright (C) 2022 Andy Boothe
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
package com.sigpwned.discourse.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

/**
 * An assignment target. A sink receives a value and stories it into the configuration object being
 * built. This is used to populate the fields of the configuration object.
 */
public interface ValueSink {

  /**
   * Returns {@code true} if this sink accepts multiple values (e.g., for a {@link List} or
   * {@link Set}), or {@code false} otherwise.
   */
  public boolean isCollection();

  public Type getGenericType();

  /**
   * Writes the value into the instance, for example by setting a field or appending to a
   * {@link List}.
   *
   * @param instance the instance to write the value into
   * @param value    the value to write
   * @throws InvocationTargetException if the value could not be written
   */
  public void write(Object instance, Object value) throws InvocationTargetException;
}
