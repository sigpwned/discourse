/*-
 * =================================LICENSE_START==================================
 * discourse-core
 * ====================================SECTION=====================================
 * Copyright (C) 2022 Andy Boothe
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
 * A configuration property that encodes its value according to a name/value pair in a
 * resolvedCommand line. For example, the following field will be set to {@code foo} if the
 * resolvedCommand line contains {@code --xray=foo}:
 * </p>
 *
 * <pre>
 * &#x40;OptionParameter(longName = "xray")
 * public String xray;
 * </pre>
 */
@Retention(RUNTIME)
@Target({METHOD, FIELD, PARAMETER})
public @interface OptionParameter {

  /**
   * @return the short name of the option, e.g., -x
   */
  public String shortName() default "";

  /**
   * @return the long name of the option, e.g., --xray
   */
  public String longName() default "";
}
