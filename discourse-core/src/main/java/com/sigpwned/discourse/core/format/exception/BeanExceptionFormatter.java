package com.sigpwned.discourse.core.format.exception;

import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.exception.BeanException;
import java.io.PrintStream;

public class BeanExceptionFormatter implements ExceptionFormatter {

  public static final BeanExceptionFormatter INSTANCE = new BeanExceptionFormatter();

  @Override
  public boolean handlesException(Throwable e) {
    return e instanceof BeanException;
  }

  @Override
  public void formatException(Throwable e, InvocationContext context) {
    BeanException bean = (BeanException) e;

    // In this case, there was a problem building the command object. This is probably a bug in
    // the application. We print a helpful error message.
    PrintStream err = context.get(InvocationContext.ERROR_STREAM_KEY).orElse(System.err);
    err.println("There was a problem building the command object.");
    err.println("You should reach out to the application developer for help.");
    err.println("They may find the following information useful:");
    err.println("ARGUMENTS: " + args);
    err.println("STACK TRACE:");
    e.printStackTrace(err);
  }
}
