package com.sigpwned.discourse.core.invocation.phase.parse.exception;

import com.sigpwned.discourse.core.exception.InternalDiscourseException;
import com.sigpwned.discourse.core.invocation.phase.parse.ArgumentsParser;

/**
 * Thrown when an {@link ArgumentsParser} is not created as expected, for example when an exception
 * is thrown during construction.
 */
public class NoArgumentsParserException extends InternalDiscourseException {
  private static final long serialVersionUID = 3820336156131527901L;

  public NoArgumentsParserException(Throwable cause) {
    super("arguments parser not available", cause);
  }
}
