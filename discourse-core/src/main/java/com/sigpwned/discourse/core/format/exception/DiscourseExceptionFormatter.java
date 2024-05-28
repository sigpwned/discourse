package com.sigpwned.discourse.core.format.exception;

import java.io.PrintStream;
import com.sigpwned.discourse.core.exception.DiscourseException;
import com.sigpwned.discourse.core.format.ExceptionFormatter;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;

public class DiscourseExceptionFormatter implements ExceptionFormatter {
  public static final DiscourseExceptionFormatter INSTANCE = new DiscourseExceptionFormatter();

  @Override
  public boolean handlesException(Throwable e, InvocationContext context) {
    return e instanceof DiscourseException;
  }

  @Override
  public void formatException(PrintStream out, Throwable e, InvocationContext context) {
    out.println(e.getLocalizedMessage());
  }
}
