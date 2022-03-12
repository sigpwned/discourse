package com.sigpwned.discourse.validation.util;

import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;

public final class MoreValidation {
  private MoreValidation() {}
  
  public static final ValidatorFactory DEFAULT_VALIDATOR_FACTORY =
      Validation.buildDefaultValidatorFactory();
}
