package com.sigpwned.discourse.core.format;

import java.io.PrintStream;
import com.sigpwned.discourse.core.Chain;
import com.sigpwned.discourse.core.pipeline.invocation.InvocationContext;

public class ExceptionFormatterChain extends Chain<ExceptionFormatter>
    implements ExceptionFormatter {

  @Override
  public boolean handlesException(Throwable e, InvocationContext context) {
    for (ExceptionFormatter formatter : this)
      if (formatter.handlesException(e, context))
        return true;
    return false;
  }

  @Override
  public void formatException(PrintStream out, Throwable e, InvocationContext context) {
    for (ExceptionFormatter formatter : this)
      if (formatter.handlesException(e, context)) {
        formatter.formatException(out, e, context);
        return;
      }
    throw new IllegalArgumentException("No exception formatter for " + e.getClass());
  }
}
