package com.sigpwned.discourse.validation.exception.argument;

import static java.lang.String.format;
import static java.util.Collections.unmodifiableSet;
import java.util.Set;
import jakarta.validation.ConstraintViolation;

public class ValidationArgumentException extends RuntimeException {
  public static String message(Set<ConstraintViolation<?>> violations) {
    if (violations.isEmpty())
      throw new IllegalArgumentException("no violations");

    ConstraintViolation<?> violation = violations.iterator().next();

    StringBuilder result = new StringBuilder();

    result.append(format("Parameter %s %s", violation.getPropertyPath(), violation.getMessage()));

    if (violations.size() > 1)
      result.append(format(" (and %d other errors)", violations.size() - 1));

    return result.toString();
  }

  private final Set<ConstraintViolation<?>> violations;

  public ValidationArgumentException(Set<ConstraintViolation<?>> violations) {
    super(message(violations));
    if (violations.isEmpty())
      throw new IllegalArgumentException("no violations");
    this.violations = unmodifiableSet(violations);
  }

  /**
   * @return the violations
   */
  public Set<ConstraintViolation<?>> getViolations() {
    return violations;
  }
}
