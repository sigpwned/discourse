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
package com.sigpwned.discourse.core.module.core.plan.value.deserializer;

import java.util.Optional;
import com.sigpwned.discourse.core.l11n.UserMessage;

/**
 * A function that deserializes a string into a value. This is used to convert resolvedCommand line
 * arguments into the appropriate types.
 */
@FunctionalInterface
public interface ValueDeserializer<T> {
  public T deserialize(String value);

  /**
   * Returns the name of the value that this deserializer can deserialize. This is useful for
   * generating help messages. For example, if this deserializer can deserialize a string into an
   * integer, the name might be {@code "integer"}.
   * 
   * @return the name of the value that this deserializer can deserialize
   */
  default Optional<UserMessage> name() {
    return Optional.empty();
  }

  /**
   * Returns an example of the value that this deserializer can deserialize. This is useful for
   * generating help messages. For example, if this deserializer can deserialize a string into an
   * integer, the example might be {@code "42"}.
   * 
   * @return an example of the value that this deserializer can deserialize
   */
  default Optional<UserMessage> example() {
    return Optional.empty();
  }
}
