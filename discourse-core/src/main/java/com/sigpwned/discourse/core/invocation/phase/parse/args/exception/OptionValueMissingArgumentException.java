package com.sigpwned.discourse.core.invocation.phase.parse.args.exception;

import com.sigpwned.discourse.core.invocation.phase.parse.args.model.coordinate.SwitchNameArgumentCoordinate;

public class OptionValueMissingArgumentException extends ArgumentException {

  private final SwitchNameArgumentCoordinate name;

  public OptionValueMissingArgumentException(SwitchNameArgumentCoordinate name) {
    super("Option \"%s\" requires a value but no value was given".formatted(name));
    this.name = name;
  }

  public SwitchNameArgumentCoordinate getName() {
    return name;
  }
}
