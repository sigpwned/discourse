package com.sigpwned.discourse.core.args.exception;

import com.sigpwned.discourse.core.args.impl.model.coordinate.SwitchNameArgumentCoordinate;

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
