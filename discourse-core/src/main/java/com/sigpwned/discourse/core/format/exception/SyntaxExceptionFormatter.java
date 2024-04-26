package com.sigpwned.discourse.core.format.exception;

import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.exception.SyntaxException;
import com.sigpwned.discourse.core.format.help.DefaultHelpFormatter;
import com.sigpwned.discourse.core.format.help.HelpFormatter;
import java.io.PrintStream;

public class SyntaxExceptionFormatter implements ExceptionFormatter {

  public static final SyntaxExceptionFormatter INSTANCE = new SyntaxExceptionFormatter();

  @Override
  public boolean handlesException(Throwable e) {
    return e instanceof SyntaxException;
  }

  @Override
  public void formatException(Throwable e, InvocationContext context) {
    SyntaxException syntax = (SyntaxException) e;
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
