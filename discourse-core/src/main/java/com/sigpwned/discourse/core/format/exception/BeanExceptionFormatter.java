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
import com.sigpwned.discourse.core.exception.BeanException;
import java.io.PrintStream;
import java.util.List;

public class BeanExceptionFormatter implements ExceptionFormatter {

  public static final BeanExceptionFormatter INSTANCE = new BeanExceptionFormatter();

  @Override
  public boolean handlesException(Throwable e, InvocationContext context) {
    return e instanceof BeanException;
  }

  @Override
  public void formatException(Throwable e, InvocationContext context) {
    // ARGUMENTS_KEY is an optional key that may or may not be present in the context. If it is
    // present, we can use it to get the arguments that were passed to the command. If it is not
    // present, we will use null instead.
    List<String> args = context.get(InvocationContext.ARGUMENTS_KEY).orElse(null);

    // In this case, there was a problem building the command object. This is probably a bug in
    // the application. We print a helpful error message.
    PrintStream err = context.get(InvocationContext.ERROR_STREAM_KEY).orElse(System.err);
    err.println("There was a problem building the command object.");
    err.println("You should reach out to the application developer for help.");
    err.println("They may find the following information useful:");
    err.println("ARGUMENTS: " + args);
    err.println("STACK TRACE:");
    e.printStackTrace(err);
  }
}
