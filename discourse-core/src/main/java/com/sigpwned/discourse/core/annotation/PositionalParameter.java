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

import com.sigpwned.discourse.core.exception.syntax.MissingRequiredParameterSyntaxException;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * <p>
 * A configuration property that appears positionally after all options and switches in the command
 * line. Parameter positions are zero-based, so position 0 is the first parameter after all options
 * and switches, position 1 is the second, and so on. For example, the following field will be set
 * to {@code foo} if the command line were {@code --flag --option1 value1 foo}:
 * </p>
 *
 * <pre>
 *   @PositionalParameter(position = 0)
 *   public String positional;
 * </pre>
 */
@Retention(RUNTIME)
@Target({FIELD, METHOD})
public @interface PositionalParameter {

  /**
   * The position of the parameter in the command line. Positions are zero-based, so position 0 is
   * the first parameter after all options and switches, position 1 is the second, and so on.
   *
   * @return the position of the parameter in the command line
   */
  public int position();

  /**
   * @return the description of the parameter
   */
  public String description() default "";

  /**
   * If the parameter is required, then it must appear on the command line, or else a
   * {@link MissingRequiredParameterSyntaxException} is thrown.
   *
   * @return whether the parameter is required
   */
  public boolean required() default true;
}
