/*-
 * =================================LICENSE_START==================================
 * discourse-validation
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
package com.sigpwned.discourse.validation.exception.argument;

import static java.lang.String.format;
import static java.util.Collections.unmodifiableSet;
import java.util.Set;
import javax.validation.ConstraintViolation;

public class ValidationArgumentException extends RuntimeException {
  private static final long serialVersionUID = 1401800639425793753L;

  public static String message(Set<ConstraintViolation<?>> violations) {
    if (violations.isEmpty())
      throw new IllegalArgumentException("no violations");

    ConstraintViolation<?> violation = violations.iterator().next();

    StringBuilder result = new StringBuilder();

    result.append(format("Parameter %s %s", violation.getPropertyPath(), violation.getMessage()));

    if (violations.size() > 1)
      result.append(format(" (and %d other errors)", violations.size() - 1));

    return result.toString();
  }

  private final Set<ConstraintViolation<?>> violations;

  public ValidationArgumentException(Set<ConstraintViolation<?>> violations) {
    super(message(violations));
    if (violations.isEmpty())
      throw new IllegalArgumentException("no violations");
    this.violations = unmodifiableSet(violations);
  }

  /**
   * @return the violations
   */
  public Set<ConstraintViolation<?>> getViolations() {
    return violations;
  }
}
