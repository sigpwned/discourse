package com.sigpwned.discourse.core.format.exception;

import com.sigpwned.discourse.core.InvocationContext;
import java.io.PrintStream;

public class CatchAllExceptionFormatter implements ExceptionFormatter {

  public static final CatchAllExceptionFormatter INSTANCE = new CatchAllExceptionFormatter();

  @Override
  public boolean handlesException(Throwable e) {
    return e instanceof Exception;
  }

  @Override
  public void formatException(Throwable e, InvocationContext context) {
    // Welp, you got me. This is probably a bug in the application? We print a helpful error message.
    PrintStream err = context.get(InvocationContext.ERROR_STREAM_KEY).orElse(System.err);
    err.println("ERROR: " + e.getMessage());
  }
}
