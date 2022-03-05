package com.sigpwned.discourse.core;

import com.sigpwned.discourse.core.annotation.OptionParameter;

/**
 * Indicates a problem with the configuration setup, such as an {@link OptionParameter} that has
 * neither a short nor long name.
 */
public abstract class ConfigurationException extends RuntimeException {
  private static final long serialVersionUID = 5876904542901684647L;

  public ConfigurationException(String message) {
    super(message);
  }
}
