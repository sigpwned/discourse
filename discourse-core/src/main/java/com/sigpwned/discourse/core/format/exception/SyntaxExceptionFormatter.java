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
import com.sigpwned.discourse.core.exception.SyntaxException;
import com.sigpwned.discourse.core.format.help.DefaultHelpFormatter;
import com.sigpwned.discourse.core.format.help.HelpFormatter;
import java.io.PrintStream;
import java.util.List;

public class SyntaxExceptionFormatter implements ExceptionFormatter {

  public static final SyntaxExceptionFormatter INSTANCE = new SyntaxExceptionFormatter();

  @Override
  public boolean handlesException(Throwable e, InvocationContext context) {
    return e instanceof SyntaxException;
  }

  @Override
  public void formatException(Throwable e, InvocationContext context) {
    SyntaxException syntax = (SyntaxException) e;

    // ARGUMENTS_KEY is an optional key that may or may not be present in the context. If it is
    // present, we can use it to get the arguments that were passed to the resolvedCommand. If it is not
    // present, we will use an empty list instead.
    List<String> args = context.get(InvocationContext.ARGUMENTS_KEY).orElse(List.of());

    // In this case, the user has made a mistake in the resolvedCommand line syntax. The resolvedCommand line
    // cannot be understood, so we do our best to figure out what the problem is and print a
    // helpful error message.
    PrintStream err = context.get(InvocationContext.ERROR_STREAM_KEY).orElse(System.err);
    if (args.isEmpty()) {
      // If there are no arguments, the user probably just wants help.
      HelpFormatter formatter = context.get(InvocationContext.HELP_FORMATTER_KEY)
          .orElse(DefaultHelpFormatter.INSTANCE);
      err.println(formatter.formatHelp(syntax.getCommand()));
    } else {
      err.println("ERROR: " + e.getMessage());
    }
  }
}
