package com.sigpwned.discourse.core.exception.argument;

import static java.lang.String.format;
import com.sigpwned.discourse.core.ArgumentException;

public class UnrecognizedShortNameArgumentException extends ArgumentException {
  private static final long serialVersionUID = 3482414372140358274L;

  private final String shortName;

  public UnrecognizedShortNameArgumentException(String shortName) {
    super(format("Unrecognized short name -%s", shortName));
    this.shortName = shortName;
  }

  /**
   * @return the shortName
   */
  public String getShortName() {
    return shortName;
  }
}
