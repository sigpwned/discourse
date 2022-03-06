package com.sigpwned.discourse.core.exception.argument;

import static java.lang.String.format;
import com.sigpwned.discourse.core.SyntaxException;

public class MissingRequiredParameterSyntaxException extends SyntaxException {
  private static final long serialVersionUID = 1055514773644141535L;
  
  private final String parameterName;

  public MissingRequiredParameterSyntaxException(String parameterName) {
    super(format("No value for the required parameter '%s'", parameterName));
    this.parameterName = parameterName;
  }

  /**
   * @return the parameterName
   */
  public String getParameterName() {
    return parameterName;
  }
}
