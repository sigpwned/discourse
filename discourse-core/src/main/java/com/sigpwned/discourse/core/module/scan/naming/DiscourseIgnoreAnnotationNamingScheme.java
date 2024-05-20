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
package com.sigpwned.discourse.core.module.scan.naming;

import java.lang.reflect.AnnotatedElement;
import java.util.Optional;
import com.sigpwned.discourse.core.annotation.DiscourseIgnore;
import com.sigpwned.discourse.core.invocation.phase.scan.NamingScheme;

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
 * {@code DiscourseIgnore}, and if it is present, then it will return {@link Optional#of(Object)
 * false}. Otherwise, it will return {@link Optional#empty() empty}.
 * </p>
 */
public class DiscourseIgnoreAnnotationNamingScheme implements NamingScheme {
  public static final DiscourseIgnoreAnnotationNamingScheme INSTANCE =
      new DiscourseIgnoreAnnotationNamingScheme();

  @Override
  public Optional<String> name(Object object) {
    if (!(object instanceof AnnotatedElement annotated))
      return Optional.empty();

    DiscourseIgnore annotation = annotated.getAnnotation(DiscourseIgnore.class);
    if (annotation == null)
      return Optional.empty();

    // TODO We need a "poison" value
    return Optional.empty();
  }
}
