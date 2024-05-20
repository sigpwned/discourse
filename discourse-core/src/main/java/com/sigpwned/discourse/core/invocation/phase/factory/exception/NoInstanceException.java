package com.sigpwned.discourse.core.invocation.phase.factory.exception;

import com.sigpwned.discourse.core.exception.InternalDiscourseException;

/**
 * Thrown when a factory executed all rules successfully, but did not create an instance. This is an
 * internal error.
 */
public class NoInstanceException extends InternalDiscourseException {
  private static final long serialVersionUID = 2286130874806523464L;

  public NoInstanceException() {
    super("No instance created");
  }
}
