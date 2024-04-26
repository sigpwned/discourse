package com.sigpwned.discourse.core.format.exception;

import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.exception.syntax.RequiredParametersMissingSyntaxException;
import com.sigpwned.discourse.core.format.help.DefaultHelpFormatter;
import com.sigpwned.discourse.core.format.help.HelpFormatter;
import java.io.PrintStream;
import java.util.List;

/**
 * An {@link ExceptionFormatter} that handles {@link RequiredParametersMissingSyntaxException}s when
 * the user provides no arguments. The formatter works under the interpretation that the user
 * doesn't know what arguments to give the command, so prints the help message for the command and
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
    System.exit(0);

  }
}
