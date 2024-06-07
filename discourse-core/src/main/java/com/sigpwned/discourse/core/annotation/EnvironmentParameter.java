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
 * A configuration option captured from the named environment variable. For example, if the
 * environment variable {@code MY_VAR} is set to {@code my value}, then the following field will be
 * set to {@code my value}:
 * </p>
 *
 * <pre>
 * &#x40;EnvironmentParameter(variableName = "MY_VAR")
 * public String myVar;
 * </pre>
 *
 * @see System#getenv(String)
 */
@Retention(RUNTIME)
@Target({FIELD, METHOD, PARAMETER})
public @interface EnvironmentParameter {
  public String variableName();
}
