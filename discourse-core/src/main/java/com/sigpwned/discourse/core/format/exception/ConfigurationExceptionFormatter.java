package com.sigpwned.discourse.core.format.exception;

import com.sigpwned.discourse.core.InvocationContext;
import com.sigpwned.discourse.core.exception.ConfigurationException;
import java.io.PrintStream;

public class ConfigurationExceptionFormatter implements ExceptionFormatter {

  public static final ConfigurationExceptionFormatter INSTANCE = new ConfigurationExceptionFormatter();

  @Override
  public boolean handlesException(Throwable e) {
    return e instanceof ConfigurationException;
  }

  @Override
  public void formatException(Throwable e, InvocationContext context) {
    ConfigurationException configuration = (ConfigurationException) e;

    // TODO Add args to the error message?
    PrintStream err = context.get(InvocationContext.ERROR_STREAM_KEY).orElse(System.err);
    err.println("There was a problem with the application configuration.");
    err.println("You should reach out to the application developer for help.");
    err.println("They may find the following information useful:");
    // err.println("ARGUMENTS: " + args);
    err.println("STACK TRACE:");
    e.printStackTrace(err);

  }
}
