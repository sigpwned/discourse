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
package com.sigpwned.discourse.core.exception.syntax;

import static java.lang.String.*;

import com.sigpwned.discourse.core.SyntaxException;

/**
 * Thrown when a parameter that requires a value is not given a value in a command line.
 */
public class MissingLongNameValueSyntaxException extends SyntaxException {

  private final String parameterName;
  private final String longName;

  public MissingLongNameValueSyntaxException(String parameterName, String longName) {
    super(format("Parameter '%s' reference --%s requires value", parameterName, longName));
    this.parameterName = parameterName;
    this.longName = longName;
  }

  /**
   * @return the parameterName
   */
  public String getParameterName() {
    return parameterName;
  }

  /**
   * @return the longName
   */
  public String getLongName() {
    return longName;
  }
}
