package com.sigpwned.discourse.core.exception.argument;

import static java.lang.String.format;
import com.sigpwned.discourse.core.SyntaxException;

public class UnrecognizedShortNameSyntaxException extends SyntaxException {
  private static final long serialVersionUID = 3482414372140358274L;

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
