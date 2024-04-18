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
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * <p>
 * A configuration property that encodes a boolean value by its presence or absence. If the flag
 * appears on the command line, then it is assigned {@code true}. Otherwise, it is assigned
 * {@code false}. For example, the following field will be set to {@code true} if the flag
 * {@code --verbose} appears on the command line:
 * </p>
 *
 * <pre>
 *   &#x40;FlagParameter(longName = "verbose")
 *   public boolean verbose;
 * </pre>
 */
@Retention(RUNTIME)
@Target({METHOD, FIELD})
public @interface FlagParameter {

  public String shortName() default "";

  public String longName() default "";

  public String description() default "";

  public boolean help() default false;

  public boolean version() default false;
}
