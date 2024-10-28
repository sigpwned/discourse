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
package com.sigpwned.discourse.core.util.collectors;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

/**
 * A collector for exactly one element. Like {@link Optional}, but with three states instead of two:
 * empty, present, and overflowed.
 *
 * @param <T> the type of the element
 */
public final class Only<T> {

  public static <T> Collector<T, ?, Only<T>> toOnly() {
    return Collector.of(Only::new, Only<T>::add, (a, b) -> {
      a.merge(b);
      return a;
    });
  }

  private static final Only<?> EMPTY = new Only<>();

  @SuppressWarnings("unchecked")
  public static <T> Only<T> empty() {
    return (Only<T>) EMPTY;
  }

  private static final Only<?> OVERFLOWED = new Only<>(2);

  @SuppressWarnings("unchecked")
  public static <T> Only<T> overflowed() {
    return (Only<T>) OVERFLOWED;
  }

  public static <T> Only<T> of(T value) {
    if (value == null) {
      throw new NullPointerException();
    }
    return ofNullable(value);
  }

  public static <T> Only<T> ofNullable(T value) {
    if (value == null) {
      return empty();
    }
    Only<T> only = new Only<>();
    only.add(value);
    return only;
  }

  public static <T> Only<T> fromIterable(Iterable<T> iterable) {
    Iterator<T> iterator = iterable.iterator();
    if (iterator.hasNext()) {
      T first = iterator.next();
      if (iterator.hasNext()) {
        return overflowed();
      }
      Only<T> result = new Only<>();
      result.add(first);
      return result;
    }
    return Only.empty();
  }

  private T firstValue;
  private int size;

  public Only() {
  }

  private Only(int size) {
    if (size < 0) {
      throw new IllegalArgumentException("size must not be negative");
    }
    this.size = size;
  }

  private void add(T value) {
    if (size == 0) {
      this.firstValue = value;
    }
    size = size + 1;
  }

  private void merge(Only<T> that) {
    if (this.size == 0) {
      this.firstValue = that.firstValue;
    }
    this.size = this.size + that.size;
  }

  public boolean isEmpty() {
    return size == 0;
  }

  public boolean isPresent() {
    return size == 1;
  }

  public boolean isOverflowed() {
    return size > 1;
  }

  public T get() {
    return orElseThrow(NoSuchElementException::new, this::newTooManyElementsException);
  }

  public Optional<T> toOptional() {
    return toOptional(this::newTooManyElementsException);
  }

  public Optional<T> toOptional(Supplier<? extends RuntimeException> multiExceptionSupplier) {
    return switch (size) {
      case 0 -> Optional.empty();
      case 1 -> Optional.ofNullable(firstValue);
      default -> throw multiExceptionSupplier.get();
    };
  }

  public T orElse(T other) {
    return orElse(other, this::newTooManyElementsException);
  }

  public T orElse(T other, Supplier<? extends RuntimeException> multiExceptionSupplier) {
    return switch (size) {
      case 0 -> other;
      case 1 -> firstValue;
      default -> throw multiExceptionSupplier.get();
    };
  }

  public T orElseGet(Supplier<? extends T> other) {
    return orElseGet(other, this::newTooManyElementsException);
  }

  public T orElseGet(Supplier<? extends T> other,
      Supplier<? extends RuntimeException> multiExceptionSupplier) {
    return switch (size) {
      case 0 -> other.get();
      case 1 -> firstValue;
      default -> throw multiExceptionSupplier.get();
    };
  }

  public T orElseThrow() {
    return orElseThrow(NoSuchElementException::new, this::newTooManyElementsException);
  }

  public T orElseThrow(Supplier<? extends RuntimeException> emptyExceptionSupplier,
      Supplier<? extends RuntimeException> multiExceptionSupplier) {
    return switch (size) {
      case 0 -> throw emptyExceptionSupplier.get();
      case 1 -> firstValue;
      default -> throw multiExceptionSupplier.get();
    };
  }

  public Stream<T> stream() {
    return stream(this::newTooManyElementsException);
  }

  public Stream<T> stream(Supplier<? extends RuntimeException> multiExceptionSupplier) {
    return switch (size) {
      case 0 -> Stream.empty();
      case 1 -> Stream.of(firstValue);
      default -> throw multiExceptionSupplier.get();
    };
  }

  public void ifPresent(Consumer<T> ifPresent,
      Supplier<? extends RuntimeException> multiExceptionSupplier) {
    ifPresentOrElse(ifPresent, () -> {
    }, multiExceptionSupplier);
  }

  public void ifPresentOrElse(Consumer<T> ifPresent, Runnable ifEmpty,
      Supplier<? extends RuntimeException> multiExceptionSupplier) {
    switch (size) {
      case 0 -> ifEmpty.run();
      case 1 -> ifPresent.accept(firstValue);
      default -> throw multiExceptionSupplier.get();
    }
  }

  public int size() {
    return size;
  }

  private RuntimeException newTooManyElementsException() {
    return new IllegalStateException("too many elements (" + size + ")");
  }
}
