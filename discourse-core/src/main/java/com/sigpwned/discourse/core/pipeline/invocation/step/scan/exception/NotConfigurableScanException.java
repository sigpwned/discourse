package com.sigpwned.discourse.core.pipeline.invocation.step.scan.exception;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.ScanException;

@SuppressWarnings("serial")
public class NotConfigurableScanException extends ScanException {
  private final Class<?> clazz;

  public NotConfigurableScanException(Class<?> clazz) {
    super(format("Class %s is not annotated with @Configurable", clazz.getName()));
    this.clazz = requireNonNull(clazz);
  }

  /**
   * @return the clazz
   */
  public Class<?> getClazz() {
    return clazz;
  }
}
