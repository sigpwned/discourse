package com.sigpwned.discourse.core.exception.syntax;

import static java.lang.String.format;
import com.sigpwned.discourse.core.SyntaxException;

public class InvalidShortNameValueSyntaxException extends SyntaxException {
  private final String parameterName;
  private final String shortName;

  public InvalidShortNameValueSyntaxException(String parameterName, String shortName) {
    super(format("Parameter '%s' reference -%s does not take a value", parameterName, shortName));
    this.parameterName = parameterName;
    this.shortName = shortName;
  }

  /**
   * @return the parameterName
   */
  public String getParameterName() {
    return parameterName;
  }

  /**
   * @return the shortName
   */
  public String getShortName() {
    return shortName;
  }
}
