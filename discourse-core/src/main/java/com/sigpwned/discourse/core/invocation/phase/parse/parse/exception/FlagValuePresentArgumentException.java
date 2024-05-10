package com.sigpwned.discourse.core.invocation.phase.parse.parse.exception;

import com.sigpwned.discourse.core.invocation.phase.parse.parse.model.coordinate.SwitchNameArgumentCoordinate;

public class FlagValuePresentArgumentException extends ArgumentException {

  private final SwitchNameArgumentCoordinate name;

  public FlagValuePresentArgumentException(SwitchNameArgumentCoordinate name) {
    super("Flag \"%s\" takes no value but one was given".formatted(name));
    this.name = name;
  }

  public SwitchNameArgumentCoordinate getName() {
    return name;
  }
}
