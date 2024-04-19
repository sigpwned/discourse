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
package com.sigpwned.discourse.core.value.sink.resolver;

import static java.util.Objects.requireNonNull;

import com.sigpwned.discourse.core.ValueSink;
import com.sigpwned.discourse.core.ValueSinkFactory;
import com.sigpwned.discourse.core.ValueSinkResolver;
import com.sigpwned.discourse.core.value.sink.AssignValueSinkFactory;
import com.sigpwned.espresso.BeanProperty;
import java.util.LinkedList;

public class DefaultValueSinkResolver implements ValueSinkResolver {

  public static class Builder {

    private final DefaultValueSinkResolver resolver;

    public Builder() {
      resolver = new DefaultValueSinkResolver();
    }

    public Builder(ValueSinkFactory defaultSink) {
      resolver = new DefaultValueSinkResolver(defaultSink);
    }

    public Builder addFirst(ValueSinkFactory storer) {
      resolver.addFirst(storer);
      return this;
    }

    public Builder addLast(ValueSinkFactory storer) {
      resolver.addLast(storer);
      return this;
    }

    public DefaultValueSinkResolver build() {
      return resolver;
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  private final LinkedList<ValueSinkFactory> sinks;
  private ValueSinkFactory defaultSink;

  public DefaultValueSinkResolver() {
    this(AssignValueSinkFactory.INSTANCE);
  }

  public DefaultValueSinkResolver(ValueSinkFactory defaultSink) {
    this.sinks = new LinkedList<>();
    this.defaultSink = requireNonNull(defaultSink);
  }

  @Override
  public ValueSink resolveValueSink(BeanProperty property) {
    return sinks.stream().filter(d -> d.isSinkable(property)).findFirst()
        .orElseGet(this::getDefaultSink)
        .getSink(property);
  }

  @Override
  public void addFirst(ValueSinkFactory deserializer) {
    sinks.remove(deserializer);
    sinks.addFirst(deserializer);
  }

  @Override
  public void addLast(ValueSinkFactory storer) {
    sinks.remove(storer);
    sinks.addLast(storer);
  }

  /**
   * @return the default sink
   */
  public ValueSinkFactory getDefaultSink() {
    return defaultSink;
  }

  @Override
  public void setDefaultSink(ValueSinkFactory defaultSink) {
    this.defaultSink = requireNonNull(defaultSink);
  }
}
