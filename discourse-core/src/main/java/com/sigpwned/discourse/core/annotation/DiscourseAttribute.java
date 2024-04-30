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
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * <p>
 * An explicit attribute name assigned to the annotated element. For example, the given method:
 * </p>
 *
 * <pre>
 *   &#064;DiscourseAttribute("foo")
 *   public int getBar() {
 *     // ...
 *   }
 * </pre>
 *
 * <p>
 * Would be assigned the attribute name "foo" despite its name apparently indicating it is a getter
 * for "bar".
 * </p>
 *
 * <p>
 * For those familiar with the Jackson library for JSON processing, this annotation is similar to
 * Jackson's {@code @JsonProperty} annotation.
 * </p>
 */
@Retention(RUNTIME)
@Target({FIELD, METHOD})
public @interface DiscourseAttribute {

  /**
   * The name of the attribute.
   *
   * @return the name of the attribute
   */
  public String value();
}
