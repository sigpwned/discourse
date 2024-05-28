package com.sigpwned.discourse.core.format.exception;

import java.io.PrintStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sigpwned.discourse.core.exception.InternalDiscourseException;
import com.sigpwned.discourse.core.format.ExceptionFormatter;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;
import com.sigpwned.discourse.core.util.Internationalization;

public class InternalDiscourseExceptionFormatter implements ExceptionFormatter {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(InternalDiscourseExceptionFormatter.class);

  public static final InternalDiscourseExceptionFormatter INSTANCE =
      new InternalDiscourseExceptionFormatter();

  @Override
  public boolean handlesException(Throwable e, InvocationContext context) {
    return e instanceof InternalDiscourseException;
  }

  @Override
  public void formatException(PrintStream out, Throwable e, InvocationContext context) {
    String prefix = Internationalization.getMessage(getClass(), "prefix").orElse(null);

    if (prefix != null) {
      out.println(prefix);
      out.println();
    } else {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("No prefix for exception formatter {}", getClass().getName());
    }

    out.println(e.getLocalizedMessage());
    out.println();
    e.printStackTrace(out);
  }
}
