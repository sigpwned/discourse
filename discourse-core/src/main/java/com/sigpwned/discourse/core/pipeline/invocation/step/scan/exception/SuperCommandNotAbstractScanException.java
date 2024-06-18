package com.sigpwned.discourse.core.pipeline.invocation.step.scan.exception;

import static java.lang.String.format;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.ScanException;

/**
 * Used when a super command class is not abstract.
 */
@SuppressWarnings("serial")
public class SuperCommandNotAbstractScanException extends ScanException {
  public SuperCommandNotAbstractScanException(Class<?> clazz) {
    super(clazz, format("Supercommand class %s must be abstract", clazz.getName()));
  }

  @Override
  protected Object[] getLocalizedMessageArguments() {
    return new Object[] {getClazz().getName()};
  }
}
