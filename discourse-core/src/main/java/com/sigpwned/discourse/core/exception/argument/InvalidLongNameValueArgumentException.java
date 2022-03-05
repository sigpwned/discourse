package com.sigpwned.discourse.core.exception.argument;

import static java.lang.String.format;
import com.sigpwned.discourse.core.ArgumentException;

public class InvalidLongNameValueArgumentException extends ArgumentException {
  private static final long serialVersionUID = -9047407425654170368L;
  
  private final String parameterName;
  private final String longName;

  public InvalidLongNameValueArgumentException(String parameterName, String longName) {
    super(format("Parameter '%s' reference --%s does not take a value", parameterName, longName));
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
