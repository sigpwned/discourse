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

import com.sigpwned.discourse.core.annotation.DiscourseAttribute;
import com.sigpwned.discourse.core.util.Streams;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 * An {@link AccessorNamingScheme} that uses the {@link DiscourseAttribute} annotation to determine
 * the names of attributes.
 * </p>
 *
 * <p>
 * When parsing a method, field, or parameter, this naming scheme will look for the
 * {@code DiscourseAttribute}, and if it is present, then it will use the value of the {@code name}
 * attribute as the name of the attribute. Otherwise, it will return
 * {@link Optional#empty() empty}.
 * </p>
 *
 * <p>
 * When matching a method, field, or parameter to an attribute, this naming scheme will look for
 * {@code DiscourseAttribute}, and if it is present, then it will compare the value of the
 * {@code name} attribute to the attribute name, and if they are equal, then it will return
 * {@link Optional#of(Object)} true}. Otherwise, it will return {@link Optional#empty() empty}.
 * </p>
 */
public class DiscourseAttributeAnnotationAccessorNamingScheme implements AccessorNamingScheme {

  public static final DiscourseAttributeAnnotationAccessorNamingScheme INSTANCE = new DiscourseAttributeAnnotationAccessorNamingScheme();

  @Override
  public Optional<String> getAttributeGetterName(String methodName, List<Annotation> annotations) {
    return findDiscourseAttributeName(annotations);
  }

  @Override
  public Optional<String> getAttributeSetterName(String methodName, List<Annotation> annotations) {
    return findDiscourseAttributeName(annotations);
  }

  @Override
  public Optional<String> getAttributeFieldName(String fieldName, List<Annotation> annotations) {
    return findDiscourseAttributeName(annotations);
  }

  @Override
  public Optional<String> getAttributeConstructorParameterName(
      List<Annotation> constructorAnnotations, String parameterName,
      List<Annotation> parameterAnnotations) {
    return findDiscourseAttributeName(parameterAnnotations);
  }

  @Override
  public Optional<String> getAttributeFactoryMethodParameterName(String methodName,
      List<Annotation> methodAnnotations, String parameterName,
      List<Annotation> parameterAnnotations) {
    return findDiscourseAttributeName(parameterAnnotations);
  }

  private Optional<String> findDiscourseAttributeName(List<Annotation> annotations) {
    return annotations.stream().mapMulti(Streams.filterAndCast(DiscourseAttribute.class))
        .map(DiscourseAttribute::value).peek(name -> {
          if (name.isEmpty()) {
            // TODO better exception
            throw new IllegalArgumentException("empty attribute name");
          }
        }).findFirst();
  }

  @Override
  public Optional<Boolean> isAttributeGetterFor(String attributeName, String methodName,
      List<Annotation> methodAnnotations) {
    return isDiscourseAttributeInvolved(attributeName, methodAnnotations);
  }

  @Override
  public Optional<Boolean> isAttributeSetterFor(String attributeName, String methodName,
      List<Annotation> methodAnnotations) {
    return isDiscourseAttributeInvolved(attributeName, methodAnnotations);
  }

  @Override
  public Optional<Boolean> isAttributeFieldFor(String attributeName, String fieldName,
      List<Annotation> fieldAnnotations) {
    return isDiscourseAttributeInvolved(attributeName, fieldAnnotations);
  }

  private Optional<Boolean> isDiscourseAttributeInvolved(String attributeName,
      List<Annotation> annotations) {
    if (findDiscourseAttributeName(annotations).map(attributeName::equals).orElse(false)) {
      return Optional.of(true);
    }
    return Optional.empty();
  }
}
