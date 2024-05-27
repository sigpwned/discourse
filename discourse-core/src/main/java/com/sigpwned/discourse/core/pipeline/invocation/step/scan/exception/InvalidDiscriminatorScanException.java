package com.sigpwned.discourse.core.pipeline.invocation.step.scan.exception;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.ScanException;

@SuppressWarnings("serial")
public class InvalidDiscriminatorScanException extends ScanException {
  private final Class<?> clazz;
  private final String invalidDiscriminator;

  public InvalidDiscriminatorScanException(Class<?> clazz, String invalidDiscriminator) {
    super(format("Class %s has invalid discriminator %s", clazz.getName(), invalidDiscriminator));
    this.clazz = requireNonNull(clazz);
    this.invalidDiscriminator = requireNonNull(invalidDiscriminator);
  }

  /**
   * @return the clazz
   */
  public Class<?> getClazz() {
    return clazz;
  }

  /**
   * @return the invalidDiscriminator
   */
  public String getInvalidDiscriminator() {
    return invalidDiscriminator;
  }
}
