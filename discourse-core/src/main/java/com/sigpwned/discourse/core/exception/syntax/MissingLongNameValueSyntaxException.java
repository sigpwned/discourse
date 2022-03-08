package com.sigpwned.discourse.core.exception.syntax;

import static java.lang.String.format;
import com.sigpwned.discourse.core.SyntaxException;

public class MissingLongNameValueSyntaxException extends SyntaxException {
  private final String parameterName;
  private final String longName;

  public MissingLongNameValueSyntaxException(String parameterName, String longName) {
    super(format("Parameter '%s' reference --%s requires value", parameterName, longName));
    this.parameterName = parameterName;
    this.longName = longName;
  }

  /**
   * @return the parameterName
   */
  public String getParameterName() {
    return parameterName;
  }

  /**
   * @return the longName
   */
  public String getLongName() {
    return longName;
  }
}
