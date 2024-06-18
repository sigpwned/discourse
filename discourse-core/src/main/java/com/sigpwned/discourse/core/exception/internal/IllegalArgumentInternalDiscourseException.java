package com.sigpwned.discourse.core.exception.internal;

import com.sigpwned.discourse.core.exception.InternalDiscourseException;

@SuppressWarnings("serial")
public class IllegalArgumentInternalDiscourseException extends InternalDiscourseException {
  public IllegalArgumentInternalDiscourseException(String message) {
    super(message);
  }
}
