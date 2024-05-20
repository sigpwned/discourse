package com.sigpwned.discourse.core.invocation.phase.factory.exception;

import com.sigpwned.discourse.core.exception.InternalDiscourseException;

public class RuleFailureException extends InternalDiscourseException {
  private static final long serialVersionUID = -5659701851737140447L;

  public RuleFailureException(Throwable cause) {
    super("Failed to execute rules", cause);
  }
}
