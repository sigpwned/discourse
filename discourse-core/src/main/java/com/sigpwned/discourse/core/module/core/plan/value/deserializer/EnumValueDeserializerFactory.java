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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EnumValueDeserializerFactory implements ValueDeserializerFactory<Enum<?>> {
  public static final EnumValueDeserializerFactory INSTANCE = new EnumValueDeserializerFactory();

  @Override
  public Optional<ValueDeserializer<? extends Enum<?>>> getDeserializer(Type genericType,
      List<Annotation> annotations) {
    List<Enum<?>> cs = null;
    if (genericType instanceof Class<?>) {
      Class<?> classType = (Class<?>) genericType;
      if (classType.getEnumConstants() != null) {
        cs = new ArrayList<>(classType.getEnumConstants().length);
        for (Object o : classType.getEnumConstants()) {
          cs.add((Enum<?>) o);
        }
      }
    }

    if (cs == null)
      return Optional.empty();

    final List<Enum<?>> constants = cs;
    return Optional.of(s -> {
      for (Enum<?> constant : constants)
        if (constant.name().equals(s))
          return constant;
      throw new IllegalArgumentException(s);
    });
  }
}
