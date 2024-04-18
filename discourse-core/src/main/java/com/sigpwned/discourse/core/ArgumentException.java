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
package com.sigpwned.discourse.core;

/**
 * An exception that is thrown when an argument is invalid. That is, the command line was parsed and
 * understood, but the specific value of the argument was not valid.
 */
public abstract class ArgumentException extends RuntimeException {

  protected ArgumentException(String message) {
    super(message);
  }

  protected ArgumentException(String message, Throwable cause) {
    super(message, cause);
  }
}
