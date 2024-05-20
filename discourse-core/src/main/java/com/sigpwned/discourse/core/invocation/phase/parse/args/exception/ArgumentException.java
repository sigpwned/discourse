package com.sigpwned.discourse.core.invocation.phase.parse.args.exception;

public abstract class ArgumentException extends RuntimeException {
  private static final long serialVersionUID = 4360501809697167440L;

  public ArgumentException(String message) {
    super(message);
  }
}
