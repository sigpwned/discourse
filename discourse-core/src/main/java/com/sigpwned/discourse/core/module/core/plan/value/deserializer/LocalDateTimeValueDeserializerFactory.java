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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Parses a {@link LocalDateTime} according to ISO-8601, e.g. 2011-12-03:10:15:30.
 * 
 * @see DateTimeFormatter#ISO_LOCAL_DATE_TIME
 */
public class LocalDateTimeValueDeserializerFactory
    implements ValueDeserializerFactory<LocalDateTime> {
  public static final LocalDateTimeValueDeserializerFactory INSTANCE =
      new LocalDateTimeValueDeserializerFactory();

  @Override
  public Optional<ValueDeserializer<? extends LocalDateTime>> getDeserializer(Type genericType,
      List<Annotation> annotations) {
    if (genericType != LocalDateTime.class)
      return Optional.empty();
    return Optional.of(LocalDateTime::parse);
  }
}
