package com.sigpwned.discourse.core;

import java.io.PrintStream;

public interface ExceptionFormatter {

  public boolean handlesException(Exception e);

  public void formatException(PrintStream err, Exception e);
}
