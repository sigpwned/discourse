package com.sigpwned.discourse.core.pipeline.invocation.step.scan.exception;

import static java.lang.String.format;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.ScanException;

/**
 * Used when a class is not expected to have a discriminator, but does
 */
@SuppressWarnings("serial")
public class UnexpectedDiscriminatorScanException extends ScanException {
  public UnexpectedDiscriminatorScanException(Class<?> clazz) {
    super(clazz,
        format("Root command class %s must not have discriminator, but does", clazz.getName()));
  }

  @Override
  protected Object[] getLocalizedMessageArguments() {
    return new Object[] {getClazz().getName()};
  }
}
