package com.sigpwned.discourse.core.format.exception;

import com.sigpwned.discourse.core.InvocationContext;

public interface ExceptionFormatter {

  public boolean handlesException(Throwable e, InvocationContext context);

  public void formatException(Throwable e, InvocationContext context);
}
