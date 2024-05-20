package com.sigpwned.discourse.core.invocation.phase.eval;

import com.sigpwned.discourse.core.exception.DiscourseException;

public abstract class EvalPhaseException extends DiscourseException {
  private static final long serialVersionUID = 38517510136530194L;

  protected EvalPhaseException(String message) {
    super(message);
  }

  protected EvalPhaseException(String message, Throwable cause) {
    super(message, cause);
  }
}
