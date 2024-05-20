package com.sigpwned.discourse.core.invocation.phase.resolve;

import com.sigpwned.discourse.core.exception.DiscourseException;

public abstract class ResolveException extends DiscourseException {
  private static final long serialVersionUID = 396482795055992488L;

  protected ResolveException(String message) {
    super(message);
  }

  protected ResolveException(String message, Throwable cause) {
    super(message, cause);
  }
}
