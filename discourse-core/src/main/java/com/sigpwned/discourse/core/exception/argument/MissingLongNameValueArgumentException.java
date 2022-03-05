package com.sigpwned.discourse.core.exception.argument;

import static java.lang.String.format;
import com.sigpwned.discourse.core.ArgumentException;

public class MissingLongNameValueArgumentException extends ArgumentException {
  private static final long serialVersionUID = -2672367856116656178L;
  
  private final String parameterName;
  private final String longName;

  public MissingLongNameValueArgumentException(String parameterName, String longName) {
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
