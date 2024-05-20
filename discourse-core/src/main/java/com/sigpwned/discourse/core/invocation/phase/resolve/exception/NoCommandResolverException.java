package com.sigpwned.discourse.core.invocation.phase.resolve.exception;

import com.sigpwned.discourse.core.exception.InternalDiscourseException;
import com.sigpwned.discourse.core.invocation.phase.resolve.CommandResolver;

/**
 * Thrown when a {@link CommandResolver} is not created as expected, for example when an exception
 * is thrown during construction.
 */
public class NoCommandResolverException extends InternalDiscourseException {
  private static final long serialVersionUID = -9208160512082488693L;

  public NoCommandResolverException(Throwable cause) {
    super("command resolver not available", cause);
  }
}
