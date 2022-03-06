package com.sigpwned.discourse.core;

import com.sigpwned.discourse.core.annotation.OptionParameter;

/**
 * Indicates a problem with the arguments given by the user, e.g. an {@link OptionParameter} was not
 * given a value on the command line
 */
public abstract class SyntaxException extends RuntimeException {
  private static final long serialVersionUID = 4695449917045882716L;
  
  public SyntaxException(String message) {
    super(message);
  }
}
