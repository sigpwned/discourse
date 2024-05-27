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
package com.sigpwned.discourse.core.module.core.plan.value.sink;

import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.Chain;
import com.sigpwned.discourse.core.util.Chains;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

/**
 * A chain of {@link ValueSinkFactory} instances. This is used to create sinks for resolvedCommand
 * line arguments. A chain can handle one or more types of values. The chain is searched in order,
 * and the first {@code ValueSinkFactory} that handles the given parameters is returned. If no
 * {@code ValueSinkFactory} in the chain handles the parameters, then the default
 * {@code ValueSinkFactory} is returned.
 */
public class ValueSinkFactoryChain extends Chain<ValueSinkFactory> implements ValueSinkFactory {

  private ValueSinkFactory defaultSink;

  public ValueSinkFactoryChain() {
    defaultSink = new AssignValueSinkFactory();
  }

  @Override
  public Optional<ValueSink> getSink(Type genericType, List<Annotation> annotations) {
    Optional<ValueSink> result = Chains.stream(this)
        .flatMap(factory -> factory.getSink(genericType, annotations).stream()).findFirst();
    if (result.isPresent()) {
      return result;
    }
    return getDefaultSink().getSink(genericType, annotations);
  }

  public void setDefaultSink(ValueSinkFactory defaultSink) {
    this.defaultSink = requireNonNull(defaultSink);
  }

  public ValueSinkFactory getDefaultSink() {
    return defaultSink;
  }
}
