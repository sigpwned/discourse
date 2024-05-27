package com.sigpwned.discourse.core.pipeline.invocation.step.scan.exception;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.ScanException;

@SuppressWarnings("serial")
public class UnexpectedDiscriminatorScanException extends ScanException {
  private final Class<?> clazz;

  public UnexpectedDiscriminatorScanException(Class<?> clazz) {
    super(format("Class %s should not have discriminator, but does", clazz.getName()));
    this.clazz = requireNonNull(clazz);
  }

  /**
   * @return the clazz
   */
  public Class<?> getClazz() {
    return clazz;
  }
}
