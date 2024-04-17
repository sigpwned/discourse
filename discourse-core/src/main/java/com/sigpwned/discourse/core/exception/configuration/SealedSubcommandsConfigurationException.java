package com.sigpwned.discourse.core.exception.configuration;

import static java.lang.String.*;

import com.sigpwned.discourse.core.ConfigurationException;

public class SealedSubcommandsConfigurationException extends ConfigurationException {

  private final Class<?> rawType;

  public SealedSubcommandsConfigurationException(Class<?> rawType) {
    super(format("Sealed configuration %s also has explicit subcommands", rawType.getName()));
    this.rawType = rawType;
  }

  public Class<?> getRawType() {
    return rawType;
  }
}
