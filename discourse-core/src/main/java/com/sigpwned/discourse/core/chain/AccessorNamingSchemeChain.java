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
package com.sigpwned.discourse.core.chain;

import com.sigpwned.discourse.core.accessor.naming.AccessorNamingScheme;
import com.sigpwned.discourse.core.util.Chains;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;

/**
 * A {@link AccessorNamingScheme} that delegates to a chain of {@code AccessorNamingScheme}
 * instances. Each link in the chain is consulted in order until one returns a non-empty result.
 */
public class AccessorNamingSchemeChain extends Chain<AccessorNamingScheme> implements
    AccessorNamingScheme {

  public Optional<String> getAttributeGetterName(String methodName, List<Annotation> annotations) {
    return Chains.stream(this)
        .flatMap(link -> link.getAttributeGetterName(methodName, annotations).stream()).findFirst();
  }

  public Optional<String> getAttributeSetterName(String methodName, List<Annotation> annotations) {
    return Chains.stream(this)
        .flatMap(link -> link.getAttributeSetterName(methodName, annotations).stream()).findFirst();
  }

  public Optional<String> getAttributeFieldName(String fieldName, List<Annotation> annotations) {
    return Chains.stream(this)
        .flatMap(link -> link.getAttributeFieldName(fieldName, annotations).stream()).findFirst();
  }

  public Optional<String> getAttributeConstructorParameterName(
      List<Annotation> constructorAnnotations, String parameterName,
      List<Annotation> parameterAnnotations) {
    return Chains.stream(this).flatMap(
        link -> link.getAttributeConstructorParameterName(constructorAnnotations, parameterName,
            parameterAnnotations).stream()).findFirst();
  }

  public Optional<String> getAttributeFactoryMethodParameterName(String methodName,
      List<Annotation> methodAnnotations, String parameterName,
      List<Annotation> parameterAnnotations) {
    return Chains.stream(this).flatMap(
        link -> link.getAttributeFactoryMethodParameterName(methodName, methodAnnotations,
            parameterName, parameterAnnotations).stream()).findFirst();
  }

  public Optional<Boolean> isAttributeGetterFor(String attributeName, String methodName,
      List<Annotation> methodAnnotations) {
    return Chains.stream(this).flatMap(
            link -> link.isAttributeGetterFor(attributeName, methodName, methodAnnotations).stream())
        .findFirst();
  }

  public Optional<Boolean> isAttributeSetterFor(String attributeName, String methodName,
      List<Annotation> methodAnnotations) {
    return Chains.stream(this).flatMap(
            link -> link.isAttributeSetterFor(attributeName, methodName, methodAnnotations).stream())
        .findFirst();
  }

  public Optional<Boolean> isAttributeFieldFor(String attributeName, String fieldName,
      List<Annotation> fieldAnnotations) {
    return Chains.stream(this).flatMap(
            link -> link.isAttributeFieldFor(attributeName, fieldName, fieldAnnotations).stream())
        .findFirst();
  }

  public Optional<Boolean> isAttributeInputFor(String attributeName, String inputName,
      List<Annotation> inputAnnotations) {
    return Chains.stream(this).flatMap(
            link -> link.isAttributeInputFor(attributeName, inputName, inputAnnotations).stream())
        .findFirst();
  }
}
