package com.sigpwned.discourse.core.format.exception;

import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.exception.ArgumentException;
import java.io.PrintStream;

public class ArgumentExceptionFormatter implements ExceptionFormatter {

  public static final ArgumentExceptionFormatter INSTANCE = new ArgumentExceptionFormatter();

  @Override
  public boolean handlesException(Throwable e) {
    return e instanceof ArgumentException;
  }

  @Override
  public void formatException(Throwable e, InvocationContext context) {
    ArgumentException argument = (ArgumentException) e;

    // In this case, the user has made a mistake in the command line arguments. The command line
    // is understood, but the arguments are not valid. We print a helpful error message.
    PrintStream err = context.get(InvocationContext.ERROR_STREAM_KEY).orElse(System.err);
    err.println("ERROR: " + e.getMessage());
  }
}
