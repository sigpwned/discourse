package com.sigpwned.discourse.core.invocation.phase.factory;

import com.sigpwned.discourse.core.exception.DiscourseException;

public abstract class FactoryPhaseException extends DiscourseException {
  private static final long serialVersionUID = 6211775858882455496L;

  protected FactoryPhaseException(String message) {
    super(message);
  }

  protected FactoryPhaseException(String message, Throwable cause) {
    super(message, cause);
  }

}
