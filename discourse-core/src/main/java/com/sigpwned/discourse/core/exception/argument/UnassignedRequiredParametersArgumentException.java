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
package com.sigpwned.discourse.core.exception.argument;

import static java.lang.String.*;
import static java.util.Collections.*;

import com.sigpwned.discourse.core.ArgumentException;
import java.util.Set;

/**
 * Thrown when a user does not provide adequate parameters to populate all the required fields of a
 * command. This can indicate missing environment variables, system properties, options, flags, or
 * positional arguments.
 */
public class UnassignedRequiredParametersArgumentException extends ArgumentException {

  private final Set<String> parameterNames;

  public UnassignedRequiredParametersArgumentException(Set<String> parameterNames) {
    super(format("The following required parameters were not given: %s", parameterNames));
    if (parameterNames.isEmpty()) {
      throw new IllegalArgumentException("parameterNames is empty");
    }
    this.parameterNames = unmodifiableSet(parameterNames);
  }

  /**
   * @return the parameterNames
   */
  public Set<String> getParameterNames() {
    return parameterNames;
  }
}
