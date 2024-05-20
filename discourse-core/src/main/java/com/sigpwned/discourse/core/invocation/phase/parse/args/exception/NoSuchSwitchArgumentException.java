package com.sigpwned.discourse.core.invocation.phase.parse.args.exception;

import com.sigpwned.discourse.core.args.coordinate.OptionCoordinate;

public class NoSuchSwitchArgumentException extends ArgumentException {
  private static final long serialVersionUID = -4179053688913408252L;

  private final OptionCoordinate name;

  public NoSuchSwitchArgumentException(OptionCoordinate name) {
    super("No such switch \"%s\"".formatted(name));
    this.name = name;
  }

  public OptionCoordinate getName() {
    return name;
  }
}
