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
package com.sigpwned.discourse.core.error;

import com.sigpwned.discourse.core.module.DefaultModule;

/**
 * Thrown defensively after {@link System#exit} is called. This is partly to appease the compiler,
 * IDEs, and other static analysis tools, and also partly for testing.
 */
public final class ExitError extends Error {

  /**
   * A factory for creating {@link ExitError} instances. Internally, discourse models exiting the
   * application (e.g., when printing a help message and exiting) as throwing a special exception
   * {@link ExitError}. This is useful (a) for massaging various and sundry compiler warnings, and
   * (b) for testing. The {@link DefaultModule default runtime configuration} simply calls
   * {@link System#exit}, but during testing, this can be overridden to return an exception
   * instead.
   */
  @FunctionalInterface
  public static interface Factory {

    public ExitError createExitError(int code);
  }

  private final int status;

  public ExitError(int status) {
    this.status = status;
  }

  public int getStatus() {
    return status;
  }
}
