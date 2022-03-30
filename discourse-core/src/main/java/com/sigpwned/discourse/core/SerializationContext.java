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
package com.sigpwned.discourse.core;

import static java.util.Collections.unmodifiableList;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class SerializationContext {
  private final LinkedList<ValueDeserializerFactory<?>> deserializers;

  public SerializationContext() {
    deserializers = new LinkedList<>();
  }

  public Optional<ValueDeserializer<?>> getDeserializer(Type genericType,
      List<Annotation> annotations) {
    return deserializers.stream().filter(d -> d.isDeserializable(genericType, annotations))
        .findFirst().map(f -> f.getDeserializer(genericType, annotations));
  }

  public void addFirst(ValueDeserializerFactory<?> deserializer) {
    deserializers.remove(deserializer);
    deserializers.addFirst(deserializer);
  }

  public void addLast(ValueDeserializerFactory<?> deserializer) {
    deserializers.remove(deserializer);
    deserializers.addLast(deserializer);
  }

  public List<ValueDeserializerFactory<?>> getDeserializers() {
    return unmodifiableList(deserializers);
  }
}
