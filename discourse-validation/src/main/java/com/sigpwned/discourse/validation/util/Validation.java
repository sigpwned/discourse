package com.sigpwned.discourse.validation.util;

import javax.validation.Validator;
import javax.validation.ValidatorFactory;

public final class Validation {

  private Validation() {
  }

  public static final ValidatorFactory DEFAULT_VALIDATOR_FACTORY = javax.validation.Validation.buildDefaultValidatorFactory();

  public static ValidatorFactory defaultValidatorFactory() {
    return DEFAULT_VALIDATOR_FACTORY;
  }

  public static Validator defaultValidator() {
    return DEFAULT_VALIDATOR_FACTORY.getValidator();
  }
}
