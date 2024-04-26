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
    // present, we can use it to get the arguments that were passed to the command. If it is not
    // present, we will use an empty list instead.
    List<String> args = context.get(InvocationContext.ARGUMENTS_KEY).orElse(List.of());

    // In this case, the user has made a mistake in the command line syntax. The command line
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
