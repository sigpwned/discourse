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

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 * An {@link AccessorNamingScheme} that uses the JavaBean naming scheme.
 * </p>
 *
 * <p>
 * The JavaBeans naming scheme is a convention for naming methods that are used to access and modify
 * the properties of a Java object. In brief, the convention is to use the prefix {@code get} or
 * {@code set} followed by the name of the property with the first letter capitalized. For example,
 * if the property is {@code name}, then the getter would be {@code getName()} and the setter would
 * be {@code setName(String name)}.
 * </p>
 *
 * <p>
 * When parsing a method, this naming scheme will inspect the method name and determine if it is a
 * getter or setter based on the prefix. If it is a getter, then it will return the name of the
 * attribute that the getter gets. If it is a setter, then it will return the name of the attribute
 * that the setter sets. Otherwise, it will return {@link Optional#empty() empty}.
 * </p>
 *
 * <p>
 * When matching a method to an attribute, this naming scheme will perform the same operation,
 * except that it will compare the attribute name to the name of the attribute that the method
 * represents. If they are equal, then it will return {@link Optional#of(Object) true}. Otherwise,
 * it will return {@link Optional#empty() empty}.
 * </p>
 *
 * <p>
 * This implementation will always return {@link Optional#empty() empty} for objects that are not
 * methods, which is to say fields and parameters.
 * </p>
 *
 * @see <a href="https://en.wikipedia.org/wiki/JavaBeans">
 * https://en.wikipedia.org/wiki/JavaBeans</a>
 */
public class FieldAccessorNamingScheme implements AccessorNamingScheme {

  public static final FieldAccessorNamingScheme INSTANCE = new FieldAccessorNamingScheme();

  @Override
  public Optional<String> getAttributeFieldName(String fieldName, List<Annotation> annotations) {
    return Optional.of(fieldName);
  }

  @Override
  public Optional<Boolean> isAttributeFieldFor(String attributeName, String fieldName,
      List<Annotation> fieldAnnotations) {
    if (fieldName.equals(attributeName)) {
      return Optional.of(true);
    }
    return Optional.empty();
  }
}
