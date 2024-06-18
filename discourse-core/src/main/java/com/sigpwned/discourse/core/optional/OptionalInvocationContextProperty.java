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
package com.sigpwned.discourse.core.optional;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import com.sigpwned.discourse.core.exception.InternalDiscourseException;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;

/**
 * An optional {@link InvocationContext invocation context} property. It is intended to be used in a
 * similar way to {@link Optional}, but for invocation context properties only. It retains the key
 * of the property, which can be useful for error messages and provides a more fluent approach to
 * application configuration.
 */
public class OptionalInvocationContextProperty<K, V> {
  /**
   * Create an optional invocation context property with the given name and value. If the value is
   * null, the optional will be empty. Otherwise, it will be present. This method is intended
   * primarily for use in testing.
   *
   * @param key the name of the invocation context property
   * @param value the value of the invocation context property
   * @param <T> the type of the invocation context property
   * @return an optional invocation context property
   */
  public static <K, V> OptionalInvocationContextProperty<K, V> of(InvocationContext.Key<K> key,
      V value) {
    return new OptionalInvocationContextProperty<>(key, value);
  }

  public static <K> OptionalInvocationContextProperty<K, K> empty(InvocationContext.Key<K> key) {
    return new OptionalInvocationContextProperty<>(key, null);
  }

  private final InvocationContext.Key<K> key;
  private final V value;

  private OptionalInvocationContextProperty(InvocationContext.Key<K> key, V value) {
    if (key == null) {
      throw new NullPointerException();
    }
    this.key = key;
    this.value = value;
  }

  public InvocationContext.Key<K> getKey() {
    return key;
  }

  public boolean isPresent() {
    return !isEmpty();
  }

  public boolean isEmpty() {
    return value == null;
  }

  public <X> OptionalInvocationContextProperty<K, X> map(Function<V, X> f) {
    return new OptionalInvocationContextProperty<>(getKey(), isPresent() ? f.apply(value) : null);
  }

  public <X> OptionalInvocationContextProperty<K, X> flatMap(Function<V, Optional<X>> f) {
    return new OptionalInvocationContextProperty<>(getKey(),
        isPresent() ? f.apply(value).orElse(null) : null);
  }

  public Stream<V> stream() {
    return isPresent() ? Stream.of(value) : Stream.empty();
  }

  public OptionalInvocationContextProperty<K, V> filter(Predicate<V> test) {
    return new OptionalInvocationContextProperty<>(getKey(),
        isPresent() && test.test(value) ? value : null);
  }

  public void ifPresent(BiConsumer<InvocationContext.Key<K>, ? super V> action) {
    ifPresentOrElse(action, key -> {
    });
  }

  public void ifPresentOrElse(BiConsumer<InvocationContext.Key<K>, ? super V> action,
      Consumer<InvocationContext.Key<K>> emptyAction) {
    if (isPresent()) {
      action.accept(getKey(), value);
    } else {
      emptyAction.accept(getKey());
    }
  }

  public OptionalInvocationContextProperty<K, V> or(
      Supplier<? extends Optional<? extends V>> supplier) {
    if (isPresent()) {
      return this;
    } else {
      @SuppressWarnings("unchecked")
      Optional<V> r = (Optional<V>) supplier.get();
      return new OptionalInvocationContextProperty<>(getKey(),
          Objects.requireNonNull(r).orElse(null));
    }
  }

  public V orElse(V defaultValue) {
    return isPresent() ? value : defaultValue;
  }

  public V orElseGet(Supplier<? extends V> defaultValue) {
    return isPresent() ? value : defaultValue.get();
  }

  public <E extends Exception> V orElseThrow(Function<InvocationContext.Key<K>, E> supplier)
      throws E {
    if (isEmpty()) {
      throw supplier.apply(getKey());
    }
    return value;
  }

  public <E extends Exception> V orElseThrow() throws E {
    return orElseThrow(key -> new InternalDiscourseException("No value for key " + key));
  }

  public V get() {
    return orElseThrow();
  }

  public Optional<V> toJavaOptional() {
    return Optional.ofNullable(value);
  }
}
