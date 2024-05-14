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
import com.sigpwned.discourse.core.error.exit.DefaultExitErrorFactory;
import com.sigpwned.discourse.core.exception.syntax.RequiredParametersMissingSyntaxException;
import com.sigpwned.discourse.core.format.help.DefaultHelpFormatter;
import com.sigpwned.discourse.core.format.help.HelpFormatter;
import java.io.PrintStream;
import java.util.List;

/**
 * An {@link ExceptionFormatter} that handles {@link RequiredParametersMissingSyntaxException}s when
 * the user provides no arguments. The formatter works under the interpretation that the user
 * doesn't know what arguments to give the resolvedCommand, so prints the help message for the resolvedCommand and
 * exits.
 */
public class EmptyArgsRequiredParametersMissingSyntaxExceptionFormatter implements
    ExceptionFormatter {

  public static final EmptyArgsRequiredParametersMissingSyntaxExceptionFormatter INSTANCE = new EmptyArgsRequiredParametersMissingSyntaxExceptionFormatter();

  @Override
  public boolean handlesException(Throwable e, InvocationContext context) {
    List<String> args = context.get(InvocationContext.ARGUMENTS_KEY).orElse(null);
    if (args != null && args.isEmpty()
        && e instanceof RequiredParametersMissingSyntaxException syntax) {
      return true;
    }
    return false;
  }

  @Override
  public void formatException(Throwable e, InvocationContext context) {
    RequiredParametersMissingSyntaxException syntax = (RequiredParametersMissingSyntaxException) e;

    PrintStream err = context.get(InvocationContext.ERROR_STREAM_KEY).orElse(System.err);
    HelpFormatter formatter = context.get(InvocationContext.HELP_FORMATTER_KEY)
        .orElse(DefaultHelpFormatter.INSTANCE);
    err.print(formatter.formatHelp(syntax.getCommand()));
    err.flush();

    // TODO Should we use a different exit code?
    throw context.get(InvocationContext.EXIT_ERROR_FACTORY_KEY)
        .orElse(DefaultExitErrorFactory.INSTANCE).createExitError(0);
  }
}
