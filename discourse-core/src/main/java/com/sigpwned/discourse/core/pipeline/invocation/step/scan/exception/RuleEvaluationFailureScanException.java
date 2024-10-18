package com.sigpwned.discourse.core.pipeline.invocation.step.scan.exception;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.ScanException;

/**
 * Used when a rule evaluation fails for application-controlled reasons.
 */
@SuppressWarnings("serial")
public class RuleEvaluationFailureScanException extends ScanException {
  private final String ruleName;

  public RuleEvaluationFailureScanException(Class<?> clazz, String ruleName, Throwable cause) {
    super(clazz, format("Command class %s rule %s evaluation failed", clazz.getName(), ruleName),
        cause);
    this.ruleName = requireNonNull(ruleName);
  }

  protected Object[] getLocalizedMessageArguments() {
    return new Object[] {getClazz().getName(), getRuleName()};
  }

  public String getRuleName() {
    return ruleName;
  }
}
