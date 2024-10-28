package com.sigpwned.discourse.core.pipeline.invocation.step.resolve;

import com.sigpwned.discourse.core.exception.DiscourseException;

@SuppressWarnings("serial")
public abstract class ResolveException extends DiscourseException {
  protected ResolveException(String message) {
    super(message);
  }

  protected ResolveException(String message, Throwable cause) {
    super(message, cause);
  }
}
