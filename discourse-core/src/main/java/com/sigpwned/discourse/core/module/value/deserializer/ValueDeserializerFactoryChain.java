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

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import com.sigpwned.discourse.core.Chain;
import com.sigpwned.discourse.core.util.Chains;

/**
 * A chain of {@link ValueDeserializerFactory} instances. This is used to create deserializers for
 * resolvedCommand line arguments. A factory can handle one or more types of values. The chain is
 * searched in order, and the first {@code ValueDeserializerFactory} that handles the given
 * parameters is returned. If no {@code ValueDeserializerFactory} in the chain handles the
 * parameters, then {@link Optional#empty() empty} is returned.
 */
public class ValueDeserializerFactoryChain extends Chain<ValueDeserializerFactory<?>>
    implements ValueDeserializerFactory<Object> {

  @Override
  @SuppressWarnings({"unchecked", "rawtypes"})
  public Optional<ValueDeserializer<? extends Object>> getDeserializer(Type genericType,
      List<Annotation> annotations) {
    return (Optional) Chains.stream(this)
        .flatMap(f -> f.getDeserializer(genericType, annotations).stream()).findFirst();
  }
}
