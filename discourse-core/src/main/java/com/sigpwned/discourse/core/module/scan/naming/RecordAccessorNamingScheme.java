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
import com.sigpwned.discourse.core.accessor.naming.AccessorNamingScheme;
import com.sigpwned.discourse.core.invocation.phase.scan.NamingScheme;

/**
 * <p>
 * An {@link AccessorNamingScheme} that uses the method name to determine if a method is an
 * attribute getter or setter.
 * </p>
 *
 * <p>
 * This implementation always returns {@link Optional#empty() empty} when parsing names.
 * </p>
 *
 * <p>
 * When matching a method to an attribute, this naming scheme will return {@link Optional#of(Object)
 * true} if the method name is the same as the attribute name. Otherwise, it will return
 * {@link Optional#empty() empty}.
 * </p>
 *
 * <p>
 * This implementation is intended to be used with
 * <a href="https://docs.oracle.com/en/java/javase/17/language/records.html">record classes</a>,
 * which have a naming scheme that is similar to the JavaBeans naming scheme, but with the
 * {@code get} and {@code set} prefixes removed.
 * </p>
 */
public class RecordAccessorNamingScheme implements NamingScheme {

  public static final RecordAccessorNamingScheme INSTANCE = new RecordAccessorNamingScheme();

  @Override
  public Optional<Boolean> isAttributeGetterFor(String attributeName, String methodName,
      List<Annotation> methodAnnotations) {
    if (methodName.equals(attributeName)) {
      return Optional.of(true);
    }
    return Optional.empty();
  }

  @Override
  public Optional<Boolean> isAttributeSetterFor(String attributeName, String methodName,
      List<Annotation> methodAnnotations) {
    if (methodName.equals(attributeName)) {
      return Optional.of(true);
    }
    return Optional.empty();
  }
}
