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

import com.sigpwned.discourse.core.annotation.OptionParameter;
import com.sigpwned.discourse.core.command.Command;

/**
 * <p>
 * Indicates a problem with the configuration setup, such as an {@link OptionParameter} that has
 * neither a short nor long name. All instances of this exception are the result of an error by the
 * programmer, not the user.
 * </p>
 *
 * <p>
 * This exception is thrown exclusively during the process creating the {@link Command} object. Once
 * the {@code Command} object is created, the configuration is considered valid and this exception
 * will no longer be thrown. The {@code Command} class either is or is not configured properly and
 * user input has nothing to do with it, so simply scanning the command class in a test is
 * sufficient to ensure that the configuration is correct.
 * </p>
 *
 * @see Command#scan(InvocationContext, Class)
 */
public abstract class ConfigurationException extends RuntimeException {

  protected ConfigurationException(String message) {
    super(message);
  }
}
