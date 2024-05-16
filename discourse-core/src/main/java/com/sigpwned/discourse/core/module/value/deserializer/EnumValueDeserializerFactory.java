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
package com.sigpwned.discourse.core.module.value.deserializer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

public class EnumValueDeserializerFactory implements ValueDeserializerFactory<Enum<?>> {
  public static final EnumValueDeserializerFactory INSTANCE=new EnumValueDeserializerFactory();
  
  @Override
  public boolean isDeserializable(Type genericType, List<Annotation> annotations) {
    if(genericType instanceof Class<?>) {
      Class<?> classType = (Class<?>) genericType;
      return classType.getEnumConstants() != null;
    }
    return false;
  }

  @Override
  public ValueDeserializer<Enum<?>> getDeserializer(Type genericType, List<Annotation> annotations) {
    Class<?> classType = (Class<?>) genericType;
    return s -> Arrays.stream(classType.getEnumConstants())
        .map(o -> (Enum<?>) o)
        .filter(e -> e.name().equals(s))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("No such enum value: "+s));
  }
}
