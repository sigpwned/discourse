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

import com.sigpwned.discourse.core.annotation.DiscourseCreator;
import com.sigpwned.discourse.core.util.ParameterAnnotations;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 * Has two primary functions:
 * </p>
 *
 * <ol>
 *   <li>
 *     Discovery. Given the name and annotations from a method, field, parameter, etc., return the
 *     name of the attribute that code element represents according to this naming scheme. For
 *     example, given a method named {@code getFoo}, this style might return {@code foo} because
 *     the method is named like a getter for the attribute {@code foo}. This style of name
 *     "discovery" is used to determine the name of all logical attributes a class defines.
 *   </li>
 *   <li>
 *     Matching. Give the name and annotations from a method, field, parameter, etc., and the name
 *     of an attribute known to exist, return whether the code element manipulates the attribute.
 *     For example, given a method {@code int foo()} and the attribute name {@code foo}, this style
 *     might return {@code true} because the method looks like a getter and has the same name as
 *     the attribute. This style of name "matching" is used to determine all the code elements
 *     associated with a logical attribute.
 *   </li>
 * </ol>
 */
public interface AccessorNamingScheme {

  /**
   * <p>
   * Given the name and annotations from a method that:
   * </p>
   *
   * <ul>
   *   <li>Has been annotated with a {@link ParameterAnnotations#isParameterAnnotation(Annotation) parameter annotation}</li>
   *   <li>Has been identified as a getter</li>
   * </ul>
   *
   * <p>
   * If the method conforms to this naming scheme, then return the name of the attribute that the
   * getter gets. Otherwise, return {@link Optional#empty() empty}.
   * </p>
   *
   * @param methodName  The name of the method
   * @param annotations The annotations on the method.
   * @return The name of the attribute that the method represents according to this naming scheme,
   * otherwise {@link Optional#empty() empty}
   */
  default Optional<String> getAttributeGetterName(String methodName, List<Annotation> annotations) {
    return Optional.empty();
  }

  default Optional<String> getAttributeSetterName(String methodName, List<Annotation> annotations) {
    return Optional.empty();
  }

  default Optional<String> getAttributeFieldName(String fieldName, List<Annotation> annotations) {
    return Optional.empty();
  }

  /**
   * <p>
   * Given the annotations on a constructor and the name and annotations from a parameter to that
   * constructor such that:
   * </p>
   *
   * <ul>
   *   <li>The constructor is a {@link DiscourseCreator creator}</li>
   *   <li>The parameter been annotated with a {@link ParameterAnnotations#isParameterAnnotation(Annotation) parameter annotation}</li>
   * </ul>
   *
   * <p>
   *   If the parameter conforms to this naming scheme, then return the name of the attribute that
   *   the parameter represents. Otherwise, return {@link Optional#empty() empty}.
   * </p>
   *
   * @param constructorAnnotations The annotations on the constructor
   * @param parameterName          The name of the parameter
   * @param parameterAnnotations   The annotations on the parameter
   * @return The name of the attribute that the parameter represents according to this naming
   * scheme, otherwise {@link Optional#empty() empty}
   */
  default Optional<String> getAttributeConstructorParameterName(
      List<Annotation> constructorAnnotations, String parameterName,
      List<Annotation> parameterAnnotations) {
    return Optional.empty();
  }


  /**
   * <p>
   * Given the name and annotations on a method and the name and annotations from a parameter to
   * that method such that:
   * </p>
   *
   * <ul>
   *   <li>The method is a factory method {@link DiscourseCreator creator}</li>
   *   <li>The parameter been annotated with a {@link ParameterAnnotations#isParameterAnnotation(Annotation) parameter annotation}</li>
   * </ul>
   *
   * <p>
   *   If the parameter conforms to this naming scheme, then return the name of the attribute that
   *   the parameter represents. Otherwise, return {@link Optional#empty() empty}.
   * </p>
   *
   * @param methodName           The name of the method
   * @param methodAnnotations    The annotations on the method
   * @param parameterName        The name of the parameter
   * @param parameterAnnotations The annotations on the parameter
   * @return The name of the attribute that the parameter represents according to this naming
   * scheme, otherwise {@link Optional#empty() empty}
   */
  default Optional<String> getAttributeFactoryMethodParameterName(String methodName,
      List<Annotation> methodAnnotations, String parameterName,
      List<Annotation> parameterAnnotations) {
    return Optional.empty();
  }

  /**
   * <p>
   * Given the name of an attribute and the name and annotations on a method such that:
   * </p>
   *
   * <ul>
   *   <li>The method has been identified a getter</li>
   *   <li>The method has not been annotated with a {@link ParameterAnnotations#isParameterAnnotation(Annotation) parameter annotation}</li>
   * </ul>
   *
   * <p>
   * If the method is a getter for the attribute according to this naming scheme, then return
   * {@code true}. If the method is not a getter for the attribute according to this naming scheme,
   * then return {@code false}. Otherwise, return {@link Optional#empty() empty}.
   * </p>
   *
   * <p>
   * If this method returns {@link Optional#empty() empty}, then it is not necessarily the case that
   * the method is not a getter for the attribute. It is only the case that the method is not a
   * getter for the attribute according to this naming scheme. Another naming scheme may consider
   * the method to be a getter for the attribute.
   * </p>
   *
   * <p>
   * However, if this method returns {@code false}, then it is the case that the method is not a
   * getter for the attribute according to any naming scheme. In other words, if this method returns
   * {@code false}, then the method is not a getter for the attribute.
   * </p>
   *
   * <p>
   * As a result, users should generally return {@code true} or {@code empty} from this method. One
   * example where a user might return {@code false} is if the naming scheme is able to determine
   * that the method explicitly is not a getter for the attribute, for example via an annotation.
   * </p>
   *
   * @param attributeName     The name of the attribute
   * @param methodName        The name of the method
   * @param methodAnnotations The annotations on the method
   * @return {@code true} if the method is a getter for the attribute according to this naming
   * scheme, otherwise {@code false}
   */
  default Optional<Boolean> isAttributeGetterFor(String attributeName, String methodName,
      List<Annotation> methodAnnotations) {
    return Optional.empty();
  }

  /**
   * <p>
   * Given the name of an attribute and the name and annotations on a method such that:
   * </p>
   *
   * <ul>
   *   <li>The method has been identified as a setter</li>
   *   <li>The method has not been annotated with a {@link ParameterAnnotations#isParameterAnnotation(Annotation) parameter annotation}</li>
   * </ul>
   *
   * <p>
   * If the method is a setter for the attribute according to this naming scheme, then return
   * {@code true}. If the method is not a setter for the attribute according to this naming scheme,
   * then return {@code false}. Otherwise, return {@link Optional#empty() empty}.
   * </p>
   *
   * <p>
   * If this method returns {@link Optional#empty() empty}, then it is not necessarily the case that
   * the method is not a setter for the attribute. It is only the case that the method is not a
   * setter for the attribute according to this naming scheme. Another naming scheme may consider
   * the method to be a setter for the attribute.
   * </p>
   *
   * <p>
   * However, if this method returns {@code false}, then it is the case that the method is not a
   * setter for the attribute according to any naming scheme. In other words, if this method returns
   * {@code false}, then the method is not a setter for the attribute.
   * </p>
   *
   * <p>
   * As a result, users should generally return {@code true} or {@code empty} from this method. One
   * example where a user might return {@code false} is if the naming scheme is able to determine
   * that the method explicitly is not a setter for the attribute, for example via an annotation.
   * </p>
   *
   * @param attributeName     The name of the attribute
   * @param methodName        The name of the method
   * @param methodAnnotations The annotations on the method
   * @return {@code true} if the method is a setter for the attribute according to this naming
   * scheme, otherwise {@code false}
   */
  default Optional<Boolean> isAttributeSetterFor(String attributeName, String methodName,
      List<Annotation> methodAnnotations) {
    return Optional.empty();
  }

  /**
   * <p>
   * Given the name of an attribute and the name and annotations on a field such that:
   * </p>
   *
   * <ul>
   *   <li>The field has not been annotated with a {@link ParameterAnnotations#isParameterAnnotation(Annotation) parameter annotation}</li>
   * </ul>
   *
   * <p>
   * If the field is an attribute field according to this naming scheme, then return {@code true}.
   * If the field is not an attribute field according to this naming scheme, then return
   * {@code false}. Otherwise, return {@link Optional#empty() empty}.
   * </p>
   *
   * <p>
   * If this method returns {@link Optional#empty() empty}, then it is not necessarily the case that
   * the field is not an attribute field. It is only the case that the field is not an attribute
   * field according to this naming scheme. Another naming scheme may consider the field to be an
   * attribute field.
   * </p>
   *
   * <p>
   * However, if this method returns {@code false}, then it is the case that the field is not an
   * attribute field according to any naming scheme. In other words, if this method returns
   * {@code false}, then the field is not an attribute field.
   * </p>
   *
   * <p>
   * As a result, users should generally return {@code true} or {@code empty} from this method. One
   * example where a user might return {@code false} is if the naming scheme is able to determine
   * that the field explicitly is not an attribute field, for example via an annotation.
   * </p>
   *
   * @param attributeName
   * @param fieldName
   * @param fieldAnnotations
   * @return
   */
  default Optional<Boolean> isAttributeFieldFor(String attributeName, String fieldName,
      List<Annotation> fieldAnnotations) {
    return Optional.empty();
  }

  default Optional<Boolean> isAttributeInputFor(String attributeName, String inputName,
      List<Annotation> inputAnnotations) {
    return Optional.empty();
  }
}
