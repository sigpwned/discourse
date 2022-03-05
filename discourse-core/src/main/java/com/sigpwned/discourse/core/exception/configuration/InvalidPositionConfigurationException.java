package com.sigpwned.discourse.core.exception.configuration;

import static java.lang.String.format;
import com.sigpwned.discourse.core.ConfigurationException;

public class InvalidPositionConfigurationException extends ConfigurationException {
  private static final long serialVersionUID = -1447492666093563777L;

  private final int position;

  public InvalidPositionConfigurationException(int position) {
    super(format("Invalid parameter position %d", position));
    this.position = position;
  }

  /**
   * @return the missingPosition
   */
  public int getPosition() {
    return position;
  }
}
