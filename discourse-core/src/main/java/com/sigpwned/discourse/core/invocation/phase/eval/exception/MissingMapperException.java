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
package com.sigpwned.discourse.core.invocation.phase.eval.exception;

import static java.util.Objects.requireNonNull;
import com.sigpwned.discourse.core.exception.InternalDiscourseException;

/**
 * Exception thrown when a required mapper is not found for the named property. This is an internal
 * error.
 */
public class MissingMapperException extends InternalDiscourseException {
  private static final long serialVersionUID = -6929390510894742483L;

  private final String propertyName;

  public MissingMapperException(String propertyName) {
    super("No mapper for property " + propertyName);
    this.propertyName = requireNonNull(propertyName);
  }

  public String getPropertyName() {
    return propertyName;
  }
}
