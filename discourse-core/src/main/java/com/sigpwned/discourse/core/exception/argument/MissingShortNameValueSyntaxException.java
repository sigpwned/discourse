package com.sigpwned.discourse.core.exception.argument;

import static java.lang.String.format;
import com.sigpwned.discourse.core.SyntaxException;

public class MissingShortNameValueSyntaxException extends SyntaxException {
  private static final long serialVersionUID = 3482414372140358274L;
  
  private final String parameterName;
  private final String shortName;

  public MissingShortNameValueSyntaxException(String parameterName, String shortName) {
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
