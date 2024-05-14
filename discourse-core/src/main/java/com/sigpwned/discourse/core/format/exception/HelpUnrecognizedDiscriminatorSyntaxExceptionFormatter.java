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
import com.sigpwned.discourse.core.exception.syntax.UnrecognizedDiscriminatorSyntaxException;
import com.sigpwned.discourse.core.format.help.DefaultHelpFormatter;
import com.sigpwned.discourse.core.format.help.HelpFormatter;
import com.sigpwned.discourse.core.util.Discriminators;
import java.io.PrintStream;

/**
 * An {@link ExceptionFormatter} that handles {@link UnrecognizedDiscriminatorSyntaxException}s when
 * the unrecognized discriminator is exactly {@link Discriminators#HELP}. The formatter works under
 * the interpretation that the user is asking for help, and prints the help message for the resolvedCommand
 * and exits.
 */
public class HelpUnrecognizedDiscriminatorSyntaxExceptionFormatter implements ExceptionFormatter {

  public static final HelpUnrecognizedDiscriminatorSyntaxExceptionFormatter INSTANCE = new HelpUnrecognizedDiscriminatorSyntaxExceptionFormatter();

  @Override
  public boolean handlesException(Throwable e, InvocationContext context) {
    if (e instanceof UnrecognizedDiscriminatorSyntaxException syntax) {
      return syntax.getDiscriminator().equals(Discriminators.HELP);
    }
    return false;
  }

  @Override
  public void formatException(Throwable e, InvocationContext context) {
    UnrecognizedDiscriminatorSyntaxException syntax = (UnrecognizedDiscriminatorSyntaxException) e;

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
