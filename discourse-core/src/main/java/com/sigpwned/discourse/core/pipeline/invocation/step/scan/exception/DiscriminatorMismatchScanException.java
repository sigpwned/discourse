package com.sigpwned.discourse.core.pipeline.invocation.step.scan.exception;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import com.sigpwned.discourse.core.command.Discriminator;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.ScanException;

@SuppressWarnings("serial")
public class DiscriminatorMismatchScanException extends ScanException {
  private final Class<?> clazz;
  private final Discriminator expectedDiscriminator;
  private final Discriminator actualDiscriminator;

  public DiscriminatorMismatchScanException(Class<?> clazz, Discriminator expectedDiscriminator,
      Discriminator actualDiscriminator) {
    super(format("Class %s should have discriminator %s, but has discriminator %s instead",
        clazz.getName(), expectedDiscriminator, actualDiscriminator));
    this.clazz = requireNonNull(clazz);
    this.expectedDiscriminator = requireNonNull(expectedDiscriminator);
    this.actualDiscriminator = requireNonNull(actualDiscriminator);
  }

  /**
   * @return the clazz
   */
  public Class<?> getClazz() {
    return clazz;
  }

  /**
   * @return the expectedDiscriminator
   */
  public Discriminator getExpectedDiscriminator() {
    return expectedDiscriminator;
  }

  /**
   * @return the actualDiscriminator
   */
  public Discriminator getActualDiscriminator() {
    return actualDiscriminator;
  }
}
