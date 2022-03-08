package com.sigpwned.discourse.core.exception.configuration;

import static java.lang.String.format;
import com.sigpwned.discourse.core.ConfigurationException;

public class MissingPositionConfigurationException extends ConfigurationException {
  private final int missingPosition;

  public MissingPositionConfigurationException(int missingPosition) {
    super(format("Missing positional parameter for position %d", missingPosition));
    this.missingPosition = missingPosition;
  }

  /**
   * @return the missingPosition
   */
  public int getMissingPosition() {
    return missingPosition;
  }
}
