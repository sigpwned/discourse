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
package com.sigpwned.discourse.core.accessor.naming;

import com.sigpwned.discourse.core.annotation.DiscourseIgnore;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 * An {@link AccessorNamingScheme} that uses the {@link DiscourseIgnore} annotation to determine if
 * a method or field should be ignored.
 * </p>
 *
 * <p>
 * This implementation always returns {@link Optional#empty() empty} when parsing names.
 * </p>
 *
 * <p>
 * When matching a method or field to an attribute, this naming scheme will look for
 * {@code DiscourseIgnore}, and if it is present, then it will return
 * {@link Optional#of(Object) false}. Otherwise, it will return {@link Optional#empty() empty}.
 * </p>
 */
public class DiscourseIgnoreAnnotationAccessorNamingScheme implements AccessorNamingScheme {

  public static final DiscourseIgnoreAnnotationAccessorNamingScheme INSTANCE = new DiscourseIgnoreAnnotationAccessorNamingScheme();

  @Override
  public Optional<Boolean> isAttributeGetterFor(String attributeName, String methodName,
      List<Annotation> methodAnnotations) {
    return isIgnored(attributeName, methodAnnotations);
  }

  @Override
  public Optional<Boolean> isAttributeSetterFor(String attributeName, String methodName,
      List<Annotation> methodAnnotations) {
    return isIgnored(attributeName, methodAnnotations);
  }

  @Override
  public Optional<Boolean> isAttributeFieldFor(String attributeName, String fieldName,
      List<Annotation> fieldAnnotations) {
    return isIgnored(attributeName, fieldAnnotations);
  }

  private Optional<Boolean> isIgnored(String attributeName, List<Annotation> annotations) {
    if (annotations.stream().anyMatch(a -> a instanceof DiscourseIgnore)) {
      return Optional.of(false);
    }
    return Optional.empty();
  }
}
