package com.sigpwned.discourse.core.invocation.phase.parse.args.exception;

import com.sigpwned.discourse.core.args.coordinate.OptionCoordinate;

public class OptionValueMissingArgumentException extends ArgumentException {
  private static final long serialVersionUID = -762134515885863323L;

  private final OptionCoordinate name;

  public OptionValueMissingArgumentException(OptionCoordinate name) {
    super("Option \"%s\" requires a value but no value was given".formatted(name));
    this.name = name;
  }

  public OptionCoordinate getName() {
    return name;
  }
}
