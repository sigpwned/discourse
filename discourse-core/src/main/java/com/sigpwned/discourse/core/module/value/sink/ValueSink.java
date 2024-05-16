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
package com.sigpwned.discourse.core.module.value.sink;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * <p>
 * An assignment target. A sink receives zero or more values and stores them for later retrieval.
 * The sink implements an arbitrary policy for combining multiple values into a single value, such
 * as:
 * </p>
 *
 * <ul>
 *   <li>{@link com.sigpwned.discourse.core.module.value.sink.AssignValueSinkFactory Overwrite}</li>
 *   <li>{@link com.sigpwned.discourse.core.module.value.sink.ArrayAppendValueSinkFactory Collect in array}</li>
 *   <li>{@link com.sigpwned.discourse.core.module.value.sink.ListAddValueSinkFactory Collect in List}</li>
 *   <li>{@link com.sigpwned.discourse.core.module.value.sink.SetAddValueSinkFactory Collect in Set}</li>
 *   <li>{@link com.sigpwned.discourse.core.module.value.sink.SortedSetAddValueSinkFactory Collect in SortedSet}</li>
 * </ul>
 */
public interface ValueSink {

  /**
   * Returns {@code true} if this sink accepts multiple values (e.g., for a {@link List} or
   * {@link Set}), or {@code false} otherwise.
   */
  public boolean isCollection();

  public Type getGenericType();

  /**
   * Writes the given value into the sink. This can perform any operation, such as overwriting the
   * current value, adding the value to a collection, etc.
   *
   * @param value the value to write
   */
  public void put(Object value);

  /**
   * Returns the value stored in the sink. If the sink is has {@link #put(Object) received} zero
   * values, this method returns an empty {@link Optional}. Otherwise, this method returns the value
   * stored in the sink.
   *
   * @return the value stored in the sink
   */
  public Optional<Object> get();
}
