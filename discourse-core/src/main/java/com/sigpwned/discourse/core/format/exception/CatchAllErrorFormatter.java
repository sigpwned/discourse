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
package com.sigpwned.discourse.core.format.exception;

import com.sigpwned.discourse.core.InvocationContext;
import java.io.PrintStream;

public class CatchAllErrorFormatter implements ExceptionFormatter {

  public static final CatchAllErrorFormatter INSTANCE = new CatchAllErrorFormatter();

  @Override
  public boolean handlesException(Throwable e, InvocationContext context) {
    return e instanceof Error;
  }

  @Override
  public void formatException(Throwable e, InvocationContext context) {
    // This is probably super serious. Errors are generally not meant to be caught, but this is a
    // CLI application, so we'll catch it and print a helpful error message.
    // TODO Add args to the error message?
    PrintStream err = context.get(InvocationContext.ERROR_STREAM_KEY).orElse(System.err);
    err.println("There was a serious problem with the application.");
    err.println("You should reach out to the application developer for help.");
    err.println("They may find the following information useful:");
    // err.println("ARGUMENTS: " + args);
    err.println("STACK TRACE:");
    e.printStackTrace(err);
  }
}
