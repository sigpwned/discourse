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

import com.sigpwned.discourse.core.annotation.EnvironmentParameter;
import com.sigpwned.discourse.core.annotation.FlagParameter;
import com.sigpwned.discourse.core.annotation.OptionParameter;
import com.sigpwned.discourse.core.annotation.PositionalParameter;
import com.sigpwned.discourse.core.annotation.PropertyParameter;
import com.sigpwned.discourse.core.util.collectors.Only;
import java.lang.annotation.Annotation;
import java.util.List;

public final class ParameterAnnotations {

  private ParameterAnnotations() {
  }

  /**
   * <p>
   * Finds the only parameter annotation in the given array of annotations. The annotation must be
   * one of the following:
   * </p>
   *
   * <ul>
   *   <li>{@link FlagParameter}</li>
   *   <li>{@link OptionParameter}</li>
   *   <li>{@link PositionalParameter}</li>
   *   <li>{@link EnvironmentParameter}</li>
   *   <li>{@link PropertyParameter}</li>
   * </ul>
   *
   * @param annotations the array of annotations to search
   * @return the first parameter annotation if there is exactly one, or {@link Only#empty()} if
   * there is zero, or {@link Only#overflowed()} if there is more than one.
   */
  public static Only<Annotation> findParameterAnnotation(Annotation[] annotations) {
    return findParameterAnnotation(List.of(annotations));
  }

  /**
   * <p>
   * Finds the only parameter annotation in the given list of annotations. The annotation must be
   * one of the following:
   * </p>
   *
   * <ul>
   *   <li>{@link FlagParameter}</li>
   *   <li>{@link OptionParameter}</li>
   *   <li>{@link PositionalParameter}</li>
   *   <li>{@link EnvironmentParameter}</li>
   *   <li>{@link PropertyParameter}</li>
   * </ul>
   *
   * @param annotations the list of annotations to search
   * @return the first parameter annotation if there is exactly one, or {@link Only#empty()} if
   * there is zero, or {@link Only#overflowed()} if there is more than one.
   * @see #findParameterAnnotation(List)
   */
  public static Only<Annotation> findParameterAnnotation(List<Annotation> annotations) {
    return annotations.stream().filter(ParameterAnnotations::isParameterAnnotation)
        .collect(Only.toOnly());
  }

  /**
   * <p>
   * Determines if the given annotation is a parameter annotation. The annotation must be one of the
   * following:
   * </p>
   *
   * <ul>
   *   <li>{@link FlagParameter}</li>
   *   <li>{@link OptionParameter}</li>
   *   <li>{@link PositionalParameter}</li>
   *   <li>{@link EnvironmentParameter}</li>
   *   <li>{@link PropertyParameter}</li>
   * </ul>
   *
   * @param annotation the annotation to check
   * @return {@code true} if the annotation is a parameter annotation, or {@code false} otherwise
   */
  public static boolean isParameterAnnotation(Annotation annotation) {
    return annotation instanceof FlagParameter || annotation instanceof OptionParameter
        || annotation instanceof PositionalParameter || annotation instanceof EnvironmentParameter
        || annotation instanceof PropertyParameter;
  }
}
