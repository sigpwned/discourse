package com.sigpwned.discourse.core.invocation.phase.scan;

import com.sigpwned.discourse.core.exception.DiscourseException;

public abstract class ScanException extends DiscourseException {
  private static final long serialVersionUID = -3751998712853634337L;

  protected ScanException(String message) {
    super(message);
  }

  protected ScanException(String message, Throwable cause) {
    super(message, cause);
  }
}
