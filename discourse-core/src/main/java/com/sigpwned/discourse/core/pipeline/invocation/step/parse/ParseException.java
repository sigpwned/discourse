package com.sigpwned.discourse.core.pipeline.invocation.step.parse;

import com.sigpwned.discourse.core.exception.UserDiscourseException;

@SuppressWarnings("serial")
public abstract class ParseException extends UserDiscourseException {
  protected ParseException(String message) {
    super(message);
  }

  protected ParseException(String message, Throwable cause) {
    super(message, cause);
  }
}
