package com.sigpwned.discourse.core.exception.syntax;

import static java.lang.String.format;
import com.sigpwned.discourse.core.SyntaxException;

public class UnrecognizedShortNameSyntaxException extends SyntaxException {
  private final String shortName;

  public UnrecognizedShortNameSyntaxException(String shortName) {
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
