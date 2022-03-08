package com.sigpwned.discourse.core.exception.syntax;

import static java.lang.String.format;
import com.sigpwned.discourse.core.SyntaxException;

public class UnrecognizedLongNameSyntaxException extends SyntaxException {
  private final String longName;

  public UnrecognizedLongNameSyntaxException(String longName) {
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
