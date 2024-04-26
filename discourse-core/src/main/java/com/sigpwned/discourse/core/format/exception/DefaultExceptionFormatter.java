package com.sigpwned.discourse.core.format.exception;

import com.sigpwned.discourse.core.InvocationContext;
import java.io.PrintStream;

public class DefaultExceptionFormatter implements ExceptionFormatter {

  public static final DefaultExceptionFormatter INSTANCE = new DefaultExceptionFormatter();

  @Override
  public boolean handlesException(Throwable e) {
    return true;
  }

  @Override
  public void formatException(Throwable e, InvocationContext context) {
    // This is the formatter of last resort, so something has gone terribly, terribly wrong.
    // But let's not let the user know, OK?
    PrintStream err = context.get(InvocationContext.ERROR_STREAM_KEY).orElse(System.err);
    e.printStackTrace(err);
  }
}
