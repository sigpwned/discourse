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

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

public class BooleanValueDeserializerFactory implements ValueDeserializerFactory<Boolean> {
  public static final BooleanValueDeserializerFactory INSTANCE =
      new BooleanValueDeserializerFactory();

  @Override
  public Optional<ValueDeserializer<? extends Boolean>> getDeserializer(Type genericType,
      List<Annotation> annotations) {
    if (genericType != boolean.class && genericType != Boolean.class)
      return Optional.empty();
    return Optional.of(new ValueDeserializer<Boolean>() {
      @Override
      public Boolean deserialize(String value) {
        return Boolean.valueOf(value);
      }

      @Override
      public Optional<String> name() {
        return Optional.of("boolean");
      }

      @Override
      public Optional<String> example() {
        return Optional.of("true");
      }
    });
  }
}
