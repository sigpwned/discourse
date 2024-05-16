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
package com.sigpwned.discourse.core.module.value.deserializer;

import com.sigpwned.discourse.core.annotation.DiscourseDeserialize;
import com.sigpwned.discourse.core.util.Streams;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

public class DiscourseDeserializeValueSerializerFactory implements
    ValueDeserializerFactory<Object> {

  public static final DiscourseDeserializeValueSerializerFactory INSTANCE = new DiscourseDeserializeValueSerializerFactory();

  @Override
  public boolean isDeserializable(Type genericType, List<Annotation> annotations) {
    return findDiscourseDeserializeAnnotation(annotations).isPresent();
  }

  @Override
  public ValueDeserializer<?> getDeserializer(Type genericType, List<Annotation> annotations) {
    DiscourseDeserialize deserialize = findDiscourseDeserializeAnnotation(
        annotations).orElseThrow();

    Class<? extends ValueDeserializer<?>> deserializeUsing = deserialize.using();

    // Whatever the type is, it needs to have an accessible default constructor
    ValueDeserializer<?> deserializer;
    try {
      deserializer = deserializeUsing.getConstructor().newInstance();
    } catch (Exception e) {
      // TODO better exception
      throw new RuntimeException(e);
    }

    return deserializer;
  }

  private Optional<DiscourseDeserialize> findDiscourseDeserializeAnnotation(
      List<Annotation> annotations) {
    return annotations.stream().mapMulti(Streams.filterAndCast(DiscourseDeserialize.class))
        .findFirst();
  }
}
