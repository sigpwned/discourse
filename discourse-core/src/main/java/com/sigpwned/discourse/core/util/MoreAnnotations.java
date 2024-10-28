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

import java.lang.annotation.Annotation;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Function;

public final class MoreAnnotations {

  private MoreAnnotations() {
  }

  /**
   * The default string value for annotations.
   */
  public static final String DEFAULT_STRING_VALUE = "";

  /**
   * Returns the string value of the given annotation. Assumes that the default value is
   * {@link #DEFAULT_STRING_VALUE}.
   *
   * @param annotation the annotation to get the value from
   * @param getter     the function to get the value from the annotation
   * @param <A>        the type of the annotation
   * @return the string value of the annotation, if not empty. Otherwise,
   * {@link Optional#empty() empty}.
   */
  public static <A extends Annotation> Optional<String> getString(A annotation,
      Function<A, String> getter) {
    return getString(annotation, getter, DEFAULT_STRING_VALUE);
  }


  /**
   * Returns the string value of the given annotation. If the value is the default value, then it
   * assumed not to have been overwritten and will be ignored.
   *
   * @param annotation the annotation to get the value from
   * @param getter     the function to get the value from the annotation
   * @param <A>        the type of the annotation
   * @return the string value of the annotation, if not the default value. Otherwise,
   * {@link Optional#empty() empty}.
   */
  public static <A extends Annotation> Optional<String> getString(A annotation,
      Function<A, String> getter, String defaultValue) {
    return Optional.ofNullable(annotation).map(getter).filter(x -> !x.equals(defaultValue));
  }

  /**
   * The default int value for annotations.
   */
  public static final int DEFAULT_INT_VALUE = Integer.MIN_VALUE;

  /**
   * Returns the int value of the given annotation. Assumes that the default value is
   * {@link Integer#MIN_VALUE}.
   *
   * @param annotation the annotation to get the value from
   * @param getter     the function to get the value from the annotation
   * @param <A>        the type of the annotation
   * @return the string value of the annotation, if not empty. Otherwise,
   * {@link Optional#empty() empty}.
   */
  public static <A extends Annotation> OptionalInt getInt(A annotation,
      Function<A, Integer> getter) {
    return getInt(annotation, getter, DEFAULT_INT_VALUE);
  }

  /**
   * Returns the int value of the given annotation. If the value is the default value, then it
   * assumed not to have been overwritten and will be ignored.
   *
   * @param annotation the annotation to get the value from
   * @param getter     the function to get the value from the annotation
   * @param <A>        the type of the annotation
   * @return the string value of the annotation, if not empty. Otherwise,
   * {@link Optional#empty() empty}.
   */
  public static <A extends Annotation> OptionalInt getInt(A annotation,
      Function<A, Integer> getter, int defaultValue) {
    if (annotation == null) {
      return OptionalInt.of(defaultValue);
    }
    int value = getter.apply(annotation);
    if (value == defaultValue) {
      return OptionalInt.empty();
    }
    return OptionalInt.of(value);
  }
}
