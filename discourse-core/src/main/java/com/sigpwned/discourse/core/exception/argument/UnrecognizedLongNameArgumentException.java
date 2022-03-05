package com.sigpwned.discourse.core.exception.argument;

import static java.lang.String.format;
import com.sigpwned.discourse.core.ArgumentException;

public class UnrecognizedLongNameArgumentException extends ArgumentException {
  private static final long serialVersionUID = -3711732780268301154L;
  
  private final String longName;

  public UnrecognizedLongNameArgumentException(String longName) {
    super(format("Unrecognized long name --%s", longName));
    this.longName = longName;
  }

  /**
   * @return the longName
   */
  public String getLongName() {
    return longName;
  }
}
