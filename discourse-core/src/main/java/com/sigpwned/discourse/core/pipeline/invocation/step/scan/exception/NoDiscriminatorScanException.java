package com.sigpwned.discourse.core.pipeline.invocation.step.scan.exception;

import static java.lang.String.format;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.ScanException;

/**
 * Used when a subcommand is expected to have a discriminator, but does not have one.
 */
@SuppressWarnings("serial")
public class NoDiscriminatorScanException extends ScanException {
  public NoDiscriminatorScanException(Class<?> clazz) {
    super(clazz, format("Subcommand class %s requires a discriminator but doesn't have one",
        clazz.getName()));
  }

  @Override
  protected Object[] getLocalizedMessageArguments() {
    return new Object[] {getClazz().getName()};
  }
}
