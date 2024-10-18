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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Parses a {@link LocalDate} according to ISO-8601, e.g. 2007-12-03.
 * 
 * @see DateTimeFormatter#ISO_LOCAL_DATE
 */
public class LocalDateValueDeserializerFactory implements ValueDeserializerFactory<LocalDate> {
  public static final LocalDateValueDeserializerFactory INSTANCE =
      new LocalDateValueDeserializerFactory();

  @Override
  public Optional<ValueDeserializer<? extends LocalDate>> getDeserializer(Type genericType,
      List<Annotation> annotations) {
    if (genericType != LocalDate.class)
      return Optional.empty();
    return Optional.of(LocalDate::parse);
  }
}
