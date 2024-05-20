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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Parses a {@link LocalTime} according to ISO-8601, e.g. 10:15.
 * 
 * @see DateTimeFormatter#ISO_LOCAL_TIME
 */
public class LocalTimeValueDeserializerFactory implements ValueDeserializerFactory<LocalTime> {
  public static final LocalTimeValueDeserializerFactory INSTANCE =
      new LocalTimeValueDeserializerFactory();

  @Override
  public Optional<ValueDeserializer<? extends LocalTime>> getDeserializer(Type genericType,
      List<Annotation> annotations) {
    if (genericType != LocalTime.class)
      return Optional.empty();
    return Optional.of(LocalTime::parse);
  }
}
