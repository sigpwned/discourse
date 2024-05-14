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
package com.sigpwned.discourse.core.chain;

import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.Chain;
import com.sigpwned.discourse.core.util.Chains;
import com.sigpwned.discourse.core.value.sink.AssignValueSinkFactory;
import com.sigpwned.discourse.core.value.sink.ValueSink;
import com.sigpwned.discourse.core.value.sink.ValueSinkFactory;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

/**
 * A chain of {@link ValueSinkFactory} instances. This is used to create sinks for resolvedCommand line
 * arguments. A chain can handle one or more types of values. The chain is searched in order, and
 * the first {@code ValueSinkFactory} that handles the given parameters is returned. If no
 * {@code ValueSinkFactory} in the chain handles the parameters, then the default
 * {@code ValueSinkFactory} is returned.
 */
public class ValueSinkFactoryChain extends Chain<ValueSinkFactory> {

  private ValueSinkFactory defaultSink;

  public ValueSinkFactoryChain() {
    defaultSink = new AssignValueSinkFactory();
  }

  public ValueSink getSink(Type genericType, List<Annotation> annotations) {
    return Chains.stream(this).filter(f -> f.isSinkable(genericType, annotations)).findFirst()
        .orElse(getDefaultSink()).getSink(genericType, annotations);

  }

  public void setDefaultSink(ValueSinkFactory defaultSink) {
    this.defaultSink = requireNonNull(defaultSink);
  }

  public ValueSinkFactory getDefaultSink() {
    return defaultSink;
  }
}
