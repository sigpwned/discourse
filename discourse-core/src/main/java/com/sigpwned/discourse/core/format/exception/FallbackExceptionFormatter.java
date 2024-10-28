package com.sigpwned.discourse.core.format.exception;

import java.io.PrintStream;
import com.sigpwned.discourse.core.format.ExceptionFormatter;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;

public class FallbackExceptionFormatter implements ExceptionFormatter {
  public static final FallbackExceptionFormatter INSTANCE = new FallbackExceptionFormatter();

  @Override
  public boolean handlesException(Throwable e, InvocationContext context) {
    return e instanceof Exception;
  }

  @Override
  public void formatException(PrintStream out, Throwable e, InvocationContext context) {
    out.println(e.getLocalizedMessage());
  }
}
