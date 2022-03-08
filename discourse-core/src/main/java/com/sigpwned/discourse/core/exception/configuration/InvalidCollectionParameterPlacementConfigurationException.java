package com.sigpwned.discourse.core.exception.configuration;

import static java.lang.String.format;
import com.sigpwned.discourse.core.ConfigurationException;

public class InvalidCollectionParameterPlacementConfigurationException extends ConfigurationException {
  private final int collectionPosition;

  public InvalidCollectionParameterPlacementConfigurationException(int collectionPosition) {
    super(format("Collection parameter at position %d is not final positional parameter",
        collectionPosition));
    this.collectionPosition = collectionPosition;
  }

  /**
   * @return the collectionPosition
   */
  public int getCollectionPosition() {
    return collectionPosition;
  }
}
