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
package com.sigpwned.discourse.core.pipeline.invocation.step.scan;

import static java.util.Objects.requireNonNull;
import com.sigpwned.discourse.core.exception.ApplicationDiscourseException;

/**
 * Exceptions in this phase are typically due to problems in the client code, such as invalid
 * configuration or a bug in the client code. Hence, we use {@link ApplicationDiscourseException} as
 * the base class for exceptions in this package.
 */
@SuppressWarnings("serial")
public abstract class ScanException extends ApplicationDiscourseException {
  private final Class<?> clazz;

  protected ScanException(Class<?> clazz, String message) {
    super(message);
    this.clazz = requireNonNull(clazz);
  }

  protected ScanException(Class<?> clazz, String message, Throwable cause) {
    super(message, cause);
    this.clazz = requireNonNull(clazz);
  }

  public Class<?> getClazz() {
    return clazz;
  }
}
