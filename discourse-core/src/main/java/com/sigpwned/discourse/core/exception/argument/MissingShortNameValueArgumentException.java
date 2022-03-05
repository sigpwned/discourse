package com.sigpwned.discourse.core.exception.argument;

import static java.lang.String.format;
import com.sigpwned.discourse.core.ArgumentException;

public class MissingShortNameValueArgumentException extends ArgumentException {
  private static final long serialVersionUID = 3482414372140358274L;
  
  private final String parameterName;
  private final String shortName;

  public MissingShortNameValueArgumentException(String parameterName, String shortName) {
    super(format("Parameter '%s' reference -%s requires value", parameterName, shortName));
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
