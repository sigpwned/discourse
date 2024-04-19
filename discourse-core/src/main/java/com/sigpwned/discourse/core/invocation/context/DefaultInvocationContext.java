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
package com.sigpwned.discourse.core.invocation.context;

import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.Module;
import com.sigpwned.discourse.core.format.help.DefaultHelpFormatter;
import com.sigpwned.discourse.core.format.version.DefaultVersionFormatter;
import com.sigpwned.discourse.core.value.deserializer.resolver.DefaultValueDeserializerResolver;
import com.sigpwned.discourse.core.value.sink.resolver.DefaultValueSinkResolver;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * <p>
 * A default implementation of {@link InvocationContext}. The default values for the required keys
 * are as follows:
 * </p>
 *
 * <ul>
 *   <li>{@link InvocationContext#HELP_FORMATTER_KEY} - {@link DefaultHelpFormatter#INSTANCE}</li>
 *   <li>{@link InvocationContext#VERSION_FORMATTER_KEY} - {@link DefaultVersionFormatter#INSTANCE}</li>
 *   <li>{@link InvocationContext#ERROR_STREAM_KEY} - {@link System#err}</li>
 *   <li>{@link InvocationContext#VALUE_DESERIALIZER_RESOLVER_KEY} - {@link DefaultValueDeserializerResolver}</li>
 *   <li>{@link InvocationContext#VALUE_SINK_RESOLVER_KEY} - {@link DefaultValueSinkResolver}</li>
 * </ul>
 */
public class DefaultInvocationContext implements InvocationContext {

  public static class Builder {

    private final DefaultInvocationContext building = new DefaultInvocationContext();

    public <T> Builder set(InvocationContext.Key<T> key, T value) {
      building.set(key, value);
      return this;
    }

    public Builder register(Module module) {
      building.register(module);
      return this;
    }

    public DefaultInvocationContext build() {
      return building;
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  private final Map<InvocationContext.Key<?>, Object> values;

  public DefaultInvocationContext() {
    this.values = new HashMap<>();
    this.values.put(InvocationContext.HELP_FORMATTER_KEY, DefaultHelpFormatter.INSTANCE);
    this.values.put(InvocationContext.VERSION_FORMATTER_KEY, DefaultVersionFormatter.INSTANCE);
    this.values.put(InvocationContext.ERROR_STREAM_KEY, System.err);
    this.values.put(InvocationContext.VALUE_DESERIALIZER_RESOLVER_KEY,
        new DefaultValueDeserializerResolver());
    this.values.put(InvocationContext.VALUE_SINK_RESOLVER_KEY, new DefaultValueSinkResolver());
  }

  @Override
  public <T> Optional<T> get(Key<T> key) {
    if (key == null) {
      throw new NullPointerException();
    }
    return Optional.ofNullable(key.type().cast(getValues().get(key)));
  }

  @Override
  public <T> void set(Key<T> key, T value) {
    if (key == null) {
      throw new NullPointerException();
    }
    if (value != null) {
      getValues().put(key, key.type().cast(value));
    } else {
      getValues().remove(key);
    }
  }

  public void register(Module module) {
    if (module == null) {
      throw new NullPointerException();
    }

    module.register(this);

    module.registerValueDeserializerFactories(
        get(InvocationContext.VALUE_DESERIALIZER_RESOLVER_KEY).orElseThrow());

    module.registerValueSinkFactories(get(InvocationContext.VALUE_SINK_RESOLVER_KEY).orElseThrow());
  }

  private Map<Key<?>, Object> getValues() {
    return values;
  }
}
