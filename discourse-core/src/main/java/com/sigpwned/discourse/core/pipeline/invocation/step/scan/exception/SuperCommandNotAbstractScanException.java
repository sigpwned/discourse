package com.sigpwned.discourse.core.pipeline.invocation.step.scan.exception;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.ScanException;

@SuppressWarnings("serial")
public class SuperCommandNotAbstractScanException extends ScanException {
  private final Class<?> clazz;

  public SuperCommandNotAbstractScanException(Class<?> clazz) {
    super(format("Subcommand class %s does not extend the supercommand class %s", clazz.getName()));
    this.clazz = requireNonNull(clazz);
  }

  public Class<?> getClazz() {
    return clazz;
  }
}
