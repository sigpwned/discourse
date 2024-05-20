package com.sigpwned.discourse.core.invocation.phase.parse;

import com.sigpwned.discourse.core.exception.DiscourseException;

/**
 * An exception that indicates a failure during the parse phase of the invocation process.
 */
public abstract class ParseException extends DiscourseException {
  private static final long serialVersionUID = -9100522084374305466L;

  protected ParseException(String message) {
    super(message);
  }

  protected ParseException(String message, Throwable cause) {
    super(message, cause);
  }
}
