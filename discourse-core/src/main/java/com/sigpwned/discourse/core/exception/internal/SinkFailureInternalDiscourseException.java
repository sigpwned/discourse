package com.sigpwned.discourse.core.exception.internal;

import static java.lang.String.format;
import com.sigpwned.discourse.core.exception.InternalDiscourseException;

@SuppressWarnings("serial")
public class SinkFailureInternalDiscourseException extends InternalDiscourseException {
  // TODO Do we want more or different information in this exception?
  public SinkFailureInternalDiscourseException(String propertyName, Exception cause) {
    super(format("Failed to sink property %s", propertyName), cause);
  }
}
