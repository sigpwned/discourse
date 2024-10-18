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
package com.sigpwned.discourse.core.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * <p>
 * An annotation that specifies a default value for an attribute.
 * </p>
 *
 * <p>
 * This annotation is provided mostly for creator parameters, which cannot be assigned a default
 * value in the Java language. Other code elements, such as fields, can simply be assigned a default
 * value in the declaration as an initializer (e.g., {@code private int x = 42;}). However, all code
 * elements can be assigned a default value using this annotation.
 * </p>
 */
@Retention(RUNTIME)
@Target({FIELD, METHOD, PARAMETER})
public @interface DiscourseDefaultValue {

  /**
   * The default value for the attribute, serialized as a string.
   *
   * @return the default value for the attribute
   * @see com.sigpwned.discourse.core.module.core.plan.value.deserializer.ValueDeserializer
   */
  public String value();
}
