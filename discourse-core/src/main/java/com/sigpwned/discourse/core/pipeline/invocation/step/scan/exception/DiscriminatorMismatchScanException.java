package com.sigpwned.discourse.core.pipeline.invocation.step.scan.exception;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import com.sigpwned.discourse.core.command.Discriminator;
import com.sigpwned.discourse.core.pipeline.invocation.step.scan.ScanException;

/**
 * Thrown when a class has a discriminator that does not match the expected discriminator from its
 * super command.
 */
@SuppressWarnings("serial")
public class DiscriminatorMismatchScanException extends ScanException {
  private final Discriminator expectedDiscriminator;
  private final Discriminator actualDiscriminator;

  public DiscriminatorMismatchScanException(Class<?> clazz, Discriminator expectedDiscriminator,
      Discriminator actualDiscriminator) {
    super(clazz, format("Class %s should have discriminator %s, but has discriminator %s instead",
        clazz.getName(), expectedDiscriminator, actualDiscriminator));
    this.expectedDiscriminator = requireNonNull(expectedDiscriminator);
    this.actualDiscriminator = requireNonNull(actualDiscriminator);
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

  @Override
  protected Object[] getLocalizedMessageArguments() {
    return new Object[] {getClazz().getName(), getExpectedDiscriminator(),
        getActualDiscriminator()};
  }
}
