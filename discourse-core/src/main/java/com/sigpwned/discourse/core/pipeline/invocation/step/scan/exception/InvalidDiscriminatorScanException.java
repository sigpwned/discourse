package com.sigpwned.discourse.core.pipeline.invocation.step.scan.exception;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.ScanException;

@SuppressWarnings("serial")
public class InvalidDiscriminatorScanException extends ScanException {
  private final String invalidDiscriminator;

  public InvalidDiscriminatorScanException(Class<?> clazz, String invalidDiscriminator) {
    super(clazz,
        format("Class %s has invalid discriminator %s", clazz.getName(), invalidDiscriminator));
    this.invalidDiscriminator = requireNonNull(invalidDiscriminator);
  }

  /**
   * @return the invalidDiscriminator
   */
  public String getInvalidDiscriminator() {
    return invalidDiscriminator;
  }

  @Override
  protected Object[] getLocalizedMessageArguments() {
    return new Object[] {getClazz().getName(), getInvalidDiscriminator()};
  }
}
