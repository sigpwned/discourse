package com.sigpwned.discourse.core.invocation.phase.parse.args.exception;

import com.sigpwned.discourse.core.args.coordinate.OptionCoordinate;

public class FlagValuePresentArgumentException extends ArgumentException {
  private static final long serialVersionUID = -577024825611557318L;

  private final OptionCoordinate name;

  public FlagValuePresentArgumentException(OptionCoordinate name) {
    super("Flag \"%s\" takes no value but one was given".formatted(name));
    this.name = name;
  }

  public OptionCoordinate getName() {
    return name;
  }
}
