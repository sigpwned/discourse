package com.sigpwned.discourse.core.invocation.phase.parse.exception;

import com.sigpwned.discourse.core.invocation.phase.parse.ParseException;

/**
 * A syntax parse exception is thrown when user syntax is invalid or ambiguous in such a way that it
 * cannot be parsed.
 */
public abstract class SyntaxParseException extends ParseException {
  private static final long serialVersionUID = 1097165523333993567L;

  protected SyntaxParseException(String message, Throwable cause) {
    super(message, cause);
  }

  protected SyntaxParseException(String message) {
    super(message);
  }
}
