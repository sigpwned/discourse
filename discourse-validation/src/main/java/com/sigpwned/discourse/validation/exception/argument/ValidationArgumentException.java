package com.sigpwned.discourse.validation.exception.argument;

import static java.lang.String.format;
import static java.util.Collections.unmodifiableSet;
import java.util.Set;
import jakarta.validation.ConstraintViolation;

public class ValidationArgumentException extends RuntimeException {
  private final Set<ConstraintViolation<?>> violations;

  public ValidationArgumentException(Set<ConstraintViolation<?>> violations) {
    super(format("Violates %d constraints", violations.size()));
    this.violations = unmodifiableSet(violations);
  }

  /**
   * @return the violations
   */
  public Set<ConstraintViolation<?>> getViolations() {
    return violations;
  }
}
