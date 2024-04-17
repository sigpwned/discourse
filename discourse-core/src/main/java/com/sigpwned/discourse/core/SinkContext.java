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

import static java.util.Collections.*;

import com.sigpwned.discourse.core.value.sink.AssignValueSinkFactory;
import com.sigpwned.espresso.BeanProperty;
import java.util.LinkedList;
import java.util.List;

/**
 * A context for managing value sinks. A value sink is an assignment target for values in a
 * configuration object. By default, there are sinks defined for fields, Lists, Sets, and arrays.
 * Users can add additional sinks to handle other types of assignment targets.
 */
public class SinkContext {

  private final LinkedList<ValueSinkFactory> sinks;
  private final ValueSinkFactory defaultSink;

  public SinkContext() {
    this(AssignValueSinkFactory.INSTANCE);
  }

  public SinkContext(ValueSinkFactory defaultSink) {
    if (defaultSink == null) {
      throw new NullPointerException();
    }
    this.sinks = new LinkedList<>();
    this.defaultSink = defaultSink;
  }

  public ValueSink getSink(BeanProperty property) {
    return sinks.stream().filter(d -> d.isSinkable(property)).findFirst().orElse(getDefaultStorer())
        .getSink(property);
  }


  public void addFirst(ValueSinkFactory deserializer) {
    sinks.remove(deserializer);
    sinks.addFirst(deserializer);
  }

  public void addLast(ValueSinkFactory storer) {
    sinks.remove(storer);
    sinks.addLast(storer);
  }

  public List<ValueSinkFactory> getStorers() {
    return unmodifiableList(sinks);
  }

  /**
   * @return the defaultStorer
   */
  private ValueSinkFactory getDefaultStorer() {
    return defaultSink;
  }
}
