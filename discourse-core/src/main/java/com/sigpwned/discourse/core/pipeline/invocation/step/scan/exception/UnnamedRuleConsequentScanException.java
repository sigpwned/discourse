package com.sigpwned.discourse.core.pipeline.invocation.step.scan.exception;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.ScanException;

/**
 * Used when a nominated syntax cannot be named
 */
@SuppressWarnings("serial")
public class UnnamedRuleConsequentScanException extends ScanException {
  private final Object nominated;

  public UnnamedRuleConsequentScanException(Class<?> clazz, Object nominated) {
    super(clazz, format("Command class %s rule nominee %s consequent could not be named",
        clazz.getName(), nominated.toString()));
    this.nominated = requireNonNull(nominated);
  }

  public Object getNominated() {
    return nominated;
  }

  @Override
  protected Object[] getLocalizedMessageArguments() {
    return new Object[] {getClazz().getName(), getNominated().toString()};
  }
}
