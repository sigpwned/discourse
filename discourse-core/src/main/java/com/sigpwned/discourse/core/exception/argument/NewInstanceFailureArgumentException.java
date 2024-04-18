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

import com.sigpwned.discourse.core.ArgumentException;

/**
 * Thrown when a new instance of a configuration class cannot be created. This is typically thrown
 * in response to an InvocationTargetException that occurs when a constructor is called.
 */
public class NewInstanceFailureArgumentException extends ArgumentException {

  // TODO This is probably more of a runtime-type exception than an argument exception
  public NewInstanceFailureArgumentException(Exception cause) {
    super("Failed to create new configuration instance", cause);
  }
}
