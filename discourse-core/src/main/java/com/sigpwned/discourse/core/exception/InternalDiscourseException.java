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
package com.sigpwned.discourse.core.exception;

/**
 * An exception that indicates a problem with the internal state of the Discourse library. This is
 * typically used to indicate a bug in the library. As such, this exception and its subclasses
 * should not be caught by client code, but should be reported to the library developers.
 */
@SuppressWarnings("serial")
public abstract class InternalDiscourseException extends DiscourseException {
  protected InternalDiscourseException(String message) {
    super(message);
  }

  protected InternalDiscourseException(String message, Throwable cause) {
    super(message, cause);
  }
}
