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
package com.sigpwned.discourse.core;

import com.sigpwned.discourse.core.error.exit.TestExitErrorFactory;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * <p>
 * A test module that sets up a test environment for the core. It makes the following changes:
 * </p>
 *
 * <ul>
 *   <li>
 *     Sets the exit error factory to {@link TestExitErrorFactory#INSTANCE} to prevent tests from
 *     accidentally exiting the build
 *   </li>
 *   <li>
 *     Sets the error stream to {@link OutputStream#nullOutputStream()} to prevent tests from
 *     accidentally writing to the console
 *   </li>
 * </ul>
 */
public class TestModule extends com.sigpwned.discourse.core.Module {

  @Override
  public void register(InvocationContext context) {
    context.set(InvocationContext.EXIT_ERROR_FACTORY_KEY, TestExitErrorFactory.INSTANCE);
    context.set(InvocationContext.ERROR_STREAM_KEY,
        new PrintStream(OutputStream.nullOutputStream()));
  }
}
