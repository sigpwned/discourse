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

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * An optional environment variable. It is intended to be used in a similar way to {@link Optional},
 * but for environment variables only. It retains the name of the environment variable, which can be
 * useful for error messages and provides a more fluent approach to application configuration.
 */
public class OptionalEnvironmentVariable<T> {

  /**
   * Get an optional environment variable with the given name. If the environment variable is not
   * set, the optional will be empty. Otherwise, it will be present.
   *
   * @param name the name of the environment variable
   * @return an optional environment variable
   */
  public static OptionalEnvironmentVariable<String> getenv(String name) {
    return new OptionalEnvironmentVariable<String>(name, System.getenv(name));
  }

  /**
   * Create an optional environment variable with the given name and value. If the value is null,
   * the optional will be empty. Otherwise, it will be present. This method is intended primarily
   * for use in testing.
   *
   * @param name  the name of the environment variable
   * @param value the value of the environment variable
   * @param <T>   the type of the environment variable
   * @return an optional environment variable
   */
  public static <T> OptionalEnvironmentVariable<T> of(String name, T value) {
    return new OptionalEnvironmentVariable<>(name, value);
  }

  private final String name;
  private final T value;

  private OptionalEnvironmentVariable(String name, T value) {
    if (name == null) {
      throw new NullPointerException();
    }
    this.name = name;
    this.value = value;
  }

  public String getName() {
    return name;
  }

  public boolean isPresent() {
    return !isEmpty();
  }

  public boolean isEmpty() {
    return value == null;
  }

  public <X> OptionalEnvironmentVariable<X> map(Function<T, X> f) {
    return new OptionalEnvironmentVariable<>(getName(), isPresent() ? f.apply(value) : null);
  }

  public <X> OptionalEnvironmentVariable<X> flatMap(Function<T, Optional<X>> f) {
    return new OptionalEnvironmentVariable<>(getName(),
        isPresent() ? f.apply(value).orElse(null) : null);
  }

  public Stream<T> stream() {
    return isPresent() ? Stream.of(value) : Stream.empty();
  }

  public OptionalEnvironmentVariable<T> filter(Predicate<T> test) {
    return new OptionalEnvironmentVariable<>(getName(),
        isPresent() && test.test(value) ? value : null);
  }

  public void ifPresent(BiConsumer<String, ? super T> action) {
    ifPresentOrElse(action, name -> {
    });
  }

  public void ifPresentOrElse(BiConsumer<String, ? super T> action, Consumer<String> emptyAction) {
    if (isPresent()) {
      action.accept(getName(), value);
    } else {
      emptyAction.accept(getName());
    }
  }

  public OptionalEnvironmentVariable<T> or(Supplier<? extends Optional<? extends T>> supplier) {
    if (isPresent()) {
      return this;
    } else {
      @SuppressWarnings("unchecked") Optional<T> r = (Optional<T>) supplier.get();
      return new OptionalEnvironmentVariable<>(getName(), Objects.requireNonNull(r).orElse(null));
    }
  }

  public T orElse(T defaultValue) {
    return isPresent() ? value : defaultValue;
  }

  public T orElseGet(Supplier<T> defaultValue) {
    return isPresent() ? value : defaultValue.get();
  }

  public <E extends Exception> T orElseThrow(Function<String, E> supplier) throws E {
    if (isEmpty()) {
      throw supplier.apply(getName());
    }
    return value;
  }

  public <E extends Exception> T orElseThrow() throws E {
    return orElseThrow(
        name -> new NoSuchElementException("No value for environment variable " + name));
  }

  public T get() {
    return orElseThrow();
  }
}
