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
package com.sigpwned.discourse.core.util;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * <p>
 * Represents a computation where the result may be {@link #isYes() positive and present},
 * {@link #isMaybe() neutral and absent}, or {@link #isNo() negative and absent}. This is similar to
 * {@link java.util.Optional} but with an additional state for negative and absent. This is useful
 * when the result of a computation is the result of a Chain of Responsibility pattern and
 * individual links in the chain should be able to say "I handled it, and here is the answer," "I
 * didn't handle it, and I don't know the answer," or "I didn't handle it, and there is no answer,
 * so stop asking."
 * </p>
 * 
 * <p>
 * The name is borrowed from Haskell, although it's not quite the same. In Haskell, a {@code Maybe}
 * is a monad that represents a computation that may fail. In this class, a {@code Maybe} represents
 * a value that may be present, absent, or absent with short-circuit.
 * </p>
 */
public final class Maybe<T> {
  private static final Maybe<?> NO = new Maybe<>(Boolean.FALSE, null);

  private static final Maybe<?> MAYBE = new Maybe<>(null, null);

  /**
   * Returns a {@link Maybe} that is {@link #isYes() positive and present} with the given value.
   * 
   * @param <T> the type of the value
   * @param value the value
   * @return a {@link Maybe} that is {@link #isYes() positive and present} with the given value
   */
  public static <T> Maybe<T> yes(T value) {
    if (value == null)
      throw new NullPointerException();
    return new Maybe<T>(Boolean.TRUE, value);
  }

  /**
   * Returns a {@link Maybe} that is {@link #isMaybe() neutral and absent}.
   * 
   * @param <T> The type of the value
   * @return a {@link Maybe} that is {@link #isMaybe() neutral and absent}
   */
  public static <T> Maybe<T> maybe() {
    return (Maybe<T>) MAYBE;
  }

  /**
   * Returns a {@link Maybe} that is {@link #yes(Object) positive and present} with the given value
   * if the value is not {@code null}, or {@link #maybe() neutral and absent} if the value is
   * {@code null}.
   * 
   * @param <T> the type of the value
   * @param value the value
   * @return a {@link Maybe} that is {@link #yes(Object) positive and present} with the given value
   *         if the value is not {@code null}, or {@link #maybe() neutral and absent} if the value
   *         is {@code null}
   */
  public static <T> Maybe<T> maybeYes(T value) {
    return value != null ? yes(value) : maybe();
  }


  /**
   * Returns a {@link Maybe} that is {@link #isNo() negative and absent}.
   * 
   * @param <T> The type of the value
   * @return a {@link Maybe} that is {@link #isNo() negative and absent}
   */
  public static <T> Maybe<T> no() {
    return (Maybe<T>) NO;
  }

  /**
   * The state of this {@link Maybe} object. If {@code null}, then the state is {@link #isMaybe()
   * neutral and absent}. If {@code true}, then the state is {@link #isYes() positive and present}.
   * If {@code false}, then the state is {@link #isNo() negative and absent}.
   */
  private final Boolean state;

  /**
   * The value of this {@link Maybe} object. If the state is {@link #isYes() positive and present},
   * then this value is not {@code null}. If the state is {@link #isMaybe() neutral and absent},
   * then this value is {@code null}. If the state is {@link #isNo() negative and absent}, then this
   * value is {@code null}.
   */
  private final T value;

  private Maybe(Boolean state, T value) {
    if (state == null && value != null)
      throw new IllegalArgumentException("state is null but value is not null");
    if (state == Boolean.TRUE && value == null)
      throw new IllegalArgumentException("state is true but value is null");
    if (state == Boolean.FALSE && value != null)
      throw new IllegalArgumentException("state is false but value is not null");
    this.state = state;
    this.value = value;
  }

  /**
   * Returns {@code true} if the state is {@link #isYes() positive and present}, or {@code false}
   * otherwise. Only one of {@link #isYes()}, {@link #isMaybe()}, or {@link #isNo()} will return
   * {@code true} for a given object.
   * 
   * @return {@code true} if the state is {@link #isYes() positive and present}, or {@code false}
   */
  public boolean isYes() {
    return state == Boolean.TRUE;
  }

  /**
   * Returns {@code true} if the state is {@link #isMaybe() neutral and absent}, or {@code false}
   * otherwise. Only one of {@link #isYes()}, {@link #isMaybe()}, or {@link #isNo()} will return
   * {@code true} for a given object.
   * 
   * @return {@code true} if the state is {@link #isMaybe() neutral and absent}, or {@code false}
   */
  public boolean isMaybe() {
    return state == null;
  }

  /**
   * Returns {@code true} if the state is {@link #isNo() negative and absent}, or {@code false}
   * otherwise. Only one of {@link #isYes()}, {@link #isMaybe()}, or {@link #isNo()} will return
   * {@code true} for a given object.
   * 
   * @return {@code true} if the state is {@link #isNo() negative and absent}, or {@code false}
   */
  public boolean isNo() {
    return state == Boolean.FALSE;
  }

  /**
   * Returns {@code true} if the state is {@link #isYes() positive and present} or {@link #isNo()
   * negative and absent}, or {@code false} otherwise.
   * 
   * @return {@code true} if the state is {@link #isYes() positive and present} or {@link #isNo()
   *         negative and absent}, or {@code false} otherwise
   */
  public boolean isDecided() {
    return isYes() || isNo();
  }

  /**
   * Returns the value if the state is {@link #isYes() positive and present}, or throws an exception
   * otherwise. Equivalent to {@link #orElseThrow()}, which is preferred.
   * 
   * @return the value if the state is {@link #isYes() positive and present}
   * @throws NoSuchElementException if the state is {@link #isMaybe() neutral and absent}
   * @throws IllegalStateException if the state is {@link #isNo() negative and absent}
   * 
   * @see #orElseThrow()
   */
  public T get() {
    return orElseThrow();
  }

  /**
   * Returns the value if the state is {@link #isYes() positive and present}, or the given value if
   * the state is {@link #isMaybe() neutral and absent}, or throws an exception otherwise.
   * 
   * @param other the value to return if the state is {@link #isMaybe() neutral and absent}
   * @return the value if the state is {@link #isYes() positive and present}, or the given value if
   *         the state is {@link #isMaybe() neutral and absent}
   * @throws IllegalStateException if the state is {@link #isNo() negative and absent}
   */
  public T orElse(T other) {
    return orElseGet(() -> other);
  }

  /**
   * Returns the value if the state is {@link #isYes() positive and present}, or the value returned
   * by the given {@link Supplier supplier} if the state is {@link #isMaybe() neutral and absent},
   * or throws an exception otherwise.
   * 
   * @param other the supplier of the value to return if the state is {@link #isMaybe() neutral and
   *        absent}
   * @return the value if the state is {@link #isYes() positive and present}, or the given value if
   *         the state is {@link #isMaybe() neutral and absent}
   * @throws IllegalStateException if the state is {@link #isNo() negative and absent}
   */
  public T orElseGet(Supplier<T> supplier) {
    if (supplier == null)
      throw new NullPointerException();
    if (value != null)
      return value;
    return supplier.get();
  }

  /**
   * Returns the value if the state is {@link #isYes() positive and present}, or throws an exception
   * otherwise.
   * 
   * @return the value if the state is {@link #isYes() positive and present}
   * @throws NoSuchElementException if the state is {@link #isMaybe() neutral and absent}
   * @throws IllegalStateException if the state is {@link #isNo() negative and absent}
   */
  public T orElseThrow() {
    return orElseThrow(() -> new NoSuchElementException());
  }

  /**
   * Returns the value if the state is {@link #isYes() positive and present}, throws the exception
   * returned by the given {@link Supplier supplier} if the state is {@link #isMaybe() neutral and
   * absent}, or throws a different exception otherwise.
   * 
   * @param supplier the supplier of the exception to throw if the state is {@link #isMaybe()
   *        neutral and absent}
   * @return the value if the state is {@link #isYes() positive and present}
   * @throws RuntimeException if the state is {@link #isNo() negative and absent}
   */
  public T orElseThrow(Supplier<RuntimeException> supplier) {
    if (supplier == null)
      throw new NullPointerException();
    if (value != null)
      return value;
    RuntimeException problem = supplier.get();
    if (problem == null)
      throw new NullPointerException();
    throw problem;
  }

  @Override
  public int hashCode() {
    return Objects.hash(state, value);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Maybe other = (Maybe) obj;
    return Objects.equals(state, other.state) && Objects.equals(value, other.value);
  }

  @Override
  public String toString() {
    return "Maybe [state=" + state + ", value=" + value + "]";
  }
}
