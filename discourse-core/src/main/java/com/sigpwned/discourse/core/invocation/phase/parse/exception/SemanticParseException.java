package com.sigpwned.discourse.core.invocation.phase.parse.exception;

import com.sigpwned.discourse.core.invocation.phase.parse.ParseException;

/**
 * A semantics parse exception is thrown when user syntax is valid, but the semantics of the syntax
 * are invalid.
 */
public abstract class SemanticParseException extends ParseException {
  private static final long serialVersionUID = 7016804966034210132L;

  protected SemanticParseException(String message, Throwable cause) {
    super(message, cause);
  }

  protected SemanticParseException(String message) {
    super(message);
  }
}
