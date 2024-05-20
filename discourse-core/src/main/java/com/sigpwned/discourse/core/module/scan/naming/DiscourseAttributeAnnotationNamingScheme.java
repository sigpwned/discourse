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
import com.sigpwned.discourse.core.annotation.DiscourseAttribute;
import com.sigpwned.discourse.core.invocation.phase.scan.NamingScheme;

/**
 * <p>
 * An {@link AccessorNamingScheme} that uses the {@link DiscourseAttribute} annotation to determine
 * the names of attributes.
 * </p>
 *
 * <p>
 * When parsing a method, field, or parameter, this naming scheme will look for
 * {@code DiscourseAttribute}, and if it is present, then it will use the value of the {@code name}
 * attribute as the name of the attribute. Otherwise, it will return {@link Optional#empty() empty}.
 * </p>
 *
 * <p>
 * When matching a method, field, or parameter to an attribute, this naming scheme will look for
 * {@code DiscourseAttribute}, and if it is present, then it will compare the value of the
 * {@code name} attribute to the attribute name, and if they are equal, then it will return
 * {@link Optional#of(Object)} true}. Otherwise, it will return {@link Optional#empty() empty}.
 * </p>
 */
public class DiscourseAttributeAnnotationNamingScheme implements NamingScheme {
  public static final DiscourseAttributeAnnotationNamingScheme INSTANCE =
      new DiscourseAttributeAnnotationNamingScheme();

  @Override
  public Optional<String> name(Object object) {
    if (!(object instanceof AnnotatedElement annotated))
      return Optional.empty();

    DiscourseAttribute annotation = annotated.getAnnotation(DiscourseAttribute.class);
    if (annotation == null)
      return Optional.empty();

    return Optional.of(annotation.value());
  }
}
