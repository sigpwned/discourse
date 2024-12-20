/*-
 * =================================LICENSE_START==================================
 * discourse-guava
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
package com.sigpwned.discourse.guava.serialization;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import com.google.common.io.ByteSource;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.sigpwned.discourse.core.module.core.plan.value.deserializer.ValueDeserializer;
import com.sigpwned.discourse.core.module.core.plan.value.deserializer.ValueDeserializerFactory;

/**
 * Deserializes a {@link ByteSource} from a {@link String} URL or local file path.
 */
public class ByteSourceValueDeserializerFactory implements ValueDeserializerFactory<ByteSource> {

  public static final ByteSourceValueDeserializerFactory INSTANCE =
      new ByteSourceValueDeserializerFactory();

  @Override
  public Optional<ValueDeserializer<? extends ByteSource>> getDeserializer(Type genericType,
      List<Annotation> annotations) {
    if (genericType != ByteSource.class)
      return Optional.empty();
    return Optional.of(s -> {
      try {
        return Resources.asByteSource(new URL(s));
      } catch (MalformedURLException e) {
        return Files.asByteSource(new File(s));
      }
    });
  }
}
