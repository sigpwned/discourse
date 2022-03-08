package com.sigpwned.discourse.core.exception.configuration;

import static java.lang.String.format;
import com.sigpwned.discourse.core.ConfigurationException;

public class InvalidRequiredParameterPlacementConfigurationException extends ConfigurationException {
  private final int requiredPosition;

  public InvalidRequiredParameterPlacementConfigurationException(int missingPosition) {
    super(format("Required parameter at position %d follows optional positional parameter",
        missingPosition));
    this.requiredPosition = missingPosition;
  }

  /**
   * @return the requiredPosition
   */
  public int getRequiredPosition() {
    return requiredPosition;
  }
}
