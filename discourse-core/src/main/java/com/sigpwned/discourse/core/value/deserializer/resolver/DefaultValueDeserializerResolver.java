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
package com.sigpwned.discourse.core.value.deserializer.resolver;

import com.sigpwned.discourse.core.ValueDeserializer;
import com.sigpwned.discourse.core.ValueDeserializerFactory;
import com.sigpwned.discourse.core.ValueDeserializerResolver;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class DefaultValueDeserializerResolver implements ValueDeserializerResolver {

  public static class Builder {

    private final DefaultValueDeserializerResolver resolver;

    public Builder() {
      resolver = new DefaultValueDeserializerResolver();
    }

    public Builder addFirst(ValueDeserializerFactory<?> deserializer) {
      resolver.addFirst(deserializer);
      return this;
    }

    public Builder addLast(ValueDeserializerFactory<?> deserializer) {
      resolver.addLast(deserializer);
      return this;
    }

    public DefaultValueDeserializerResolver build() {
      return resolver;
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  private final LinkedList<ValueDeserializerFactory<?>> deserializers;

  public DefaultValueDeserializerResolver() {
    deserializers = new LinkedList<>();
  }

  @Override
  public Optional<ValueDeserializer<?>> resolveValueDeserializer(Type genericType,
      List<Annotation> annotations) {
    return getDeserializers().stream().filter(d -> d.isDeserializable(genericType, annotations))
        .findFirst().map(f -> f.getDeserializer(genericType, annotations));
  }

  @Override
  public void addFirst(ValueDeserializerFactory<?> deserializer) {
    getDeserializers().remove(deserializer);
    getDeserializers().addFirst(deserializer);
  }

  @Override
  public void addLast(ValueDeserializerFactory<?> deserializer) {
    getDeserializers().remove(deserializer);
    getDeserializers().addLast(deserializer);
  }

  private LinkedList<ValueDeserializerFactory<?>> getDeserializers() {
    return deserializers;
  }
}
