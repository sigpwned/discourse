package com.sigpwned.discourse.core.phase.parse.exception;

import com.sigpwned.discourse.core.phase.parse.model.coordinate.SwitchNameArgumentCoordinate;

public class NoSuchSwitchArgumentException extends ArgumentException {

  private final SwitchNameArgumentCoordinate name;

  public NoSuchSwitchArgumentException(SwitchNameArgumentCoordinate name) {
    super("No such switch \"%s\"".formatted(name));
    this.name = name;
  }

  public SwitchNameArgumentCoordinate getName() {
    return name;
  }
}
