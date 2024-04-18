/*-
 * =================================LICENSE_START==================================
 * discourse-guava
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
package com.sigpwned.discourse.guava;

import com.sigpwned.discourse.core.Module;
import com.sigpwned.discourse.core.SerializationContext;
import com.sigpwned.discourse.guava.serialization.ByteSourceValueDeserializerFactory;

/**
 * An example {@link Module} for deserializing a handful of Guava types.
 */
public class GuavaModule extends Module {

  /**
   * <p>
   * Registers the Guava deserializers:
   * </p>
   *
   * <ul>
   *   <li>{@link ByteSourceValueDeserializerFactory}</li>
   * </ul>
   */
  @Override
  public void register(SerializationContext context) {
    context.addLast(ByteSourceValueDeserializerFactory.INSTANCE);
  }
}
