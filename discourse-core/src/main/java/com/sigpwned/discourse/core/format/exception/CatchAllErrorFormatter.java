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
