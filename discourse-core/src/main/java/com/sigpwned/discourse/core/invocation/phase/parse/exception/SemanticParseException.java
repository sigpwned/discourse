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
package com.sigpwned.discourse.core.invocation.phase.parse.exception;

import com.sigpwned.discourse.core.invocation.phase.parse.ParseException;

/**
 * A semantics parse exception is thrown when user syntax is valid, but the semantics of the syntax
 * are invalid.
 */
public abstract class SemanticParseException extends ParseException {
  private static final long serialVersionUID = 7016804966034210132L;

  protected SemanticParseException(String message, Throwable cause) {
    super(message, cause);
  }

  protected SemanticParseException(String message) {
    super(message);
  }
}
