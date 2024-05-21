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

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import com.sigpwned.discourse.core.invocation.phase.scan.NamingScheme;
import com.sigpwned.discourse.core.util.Maybe;

/**
 * <p>
 * An {@link AccessorNamingScheme} that uses parameter names to determine the names of attributes.
 * </p>
 *
 * <p>
 * This naming scheme will use the name of the parameter as the name of the attribute. If the
 * parameter is synthetic, then it will return {@link Optional#empty() empty}.
 * </p>
 *
 * @see <a href=
 *      "https://docs.oracle.com/javase/tutorial/reflect/member/methodparameterreflection.html">
 *      https://docs.oracle.com/javase/tutorial/reflect/member/methodparameterreflection.html</a>
 */
public class ParameterNamingScheme implements NamingScheme {

  public static final ParameterNamingScheme INSTANCE = new ParameterNamingScheme();

  /**
   * @see <a href=
   *      "https://docs.oracle.com/javase/tutorial/reflect/member/methodparameterreflection.html">
   *      https://docs.oracle.com/javase/tutorial/reflect/member/methodparameterreflection.html</a>
   */
  private static final Pattern SYNTHETIC_PARAMETER_NAME_PATTERN = Pattern.compile("arg\\d+");

  @Override
  public Maybe<String> getAttributeConstructorParameterName(List<Annotation> constructorAnnotations,
      String parameterName, List<Annotation> parameterAnnotations) {
    if (SYNTHETIC_PARAMETER_NAME_PATTERN.matcher(parameterName).matches()) {
      return Maybe.maybe();
    }
    return Maybe.yes(parameterName);
  }

  @Override
  public Optional<String> getAttributeFactoryMethodParameterName(String methodName,
      List<Annotation> methodAnnotations, String parameterName,
      List<Annotation> parameterAnnotations) {
    if (SYNTHETIC_PARAMETER_NAME_PATTERN.matcher(parameterName).matches()) {
      return Optional.empty();
    }
    return Optional.of(parameterName);
  }
}
